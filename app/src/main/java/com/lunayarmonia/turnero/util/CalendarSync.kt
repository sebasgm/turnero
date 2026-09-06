package com.lunayarmonia.turnero.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import com.lunayarmonia.turnero.data.Paciente
import com.lunayarmonia.turnero.data.Turno
import java.time.ZoneId
import java.util.TimeZone

/**
 * A diferencia del .ics (que se comparte con la paciente), esto escribe el
 * turno directo en un calendario que ya está sincronizado en el celular de
 * quien da los turnos — típicamente su cuenta de Google. No hace falta
 * ningún login ni API key: Android expone este acceso a través del
 * Calendar Provider, siempre que la app tenga permiso READ/WRITE_CALENDAR.
 */
object CalendarSync {

    data class CalendarioDisponible(val id: Long, val email: String, val nombre: String, val esPrimario: Boolean)

    /** Lista los calendarios editables disponibles en el dispositivo, para que la usuaria elija uno en Perfil. */
    fun listarCalendariosDisponibles(context: Context): List<CalendarioDisponible> {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.IS_PRIMARY,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
        )
        val resultado = mutableListOf<CalendarioDisponible>()
        context.contentResolver.query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    val accessLevel = cursor.getInt(4)
                    if (accessLevel >= CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR) {
                        resultado.add(
                            CalendarioDisponible(
                                id = cursor.getLong(0),
                                email = cursor.getString(1) ?: "",
                                nombre = cursor.getString(2) ?: "",
                                esPrimario = cursor.getInt(3) == 1
                            )
                        )
                    }
                }
            }
        return resultado
    }

    /**
     * A qué calendario se escribe cuando se crea un turno: primero respeta lo
     * elegido en Perfil (PreferenciasCalendario); si nunca se eligió nada
     * ahí, cae de vuelta al primario del dispositivo como antes.
     */
    suspend fun calendarioDestinoId(context: Context): Long? {
        val elegido = PreferenciasCalendario.obtenerIdGuardado(context)
        if (elegido != null) return elegido
        return calendarioPrimarioAutomatico(context)
    }

    private fun calendarioPrimarioAutomatico(context: Context): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
        )
        context.contentResolver.query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null)
            ?.use { cursor ->
                var candidato: Long? = null
                while (cursor.moveToNext()) {
                    val accessLevel = cursor.getInt(2)
                    if (accessLevel >= CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR) {
                        val id = cursor.getLong(0)
                        val esPrimario = cursor.getInt(1) == 1
                        if (esPrimario) return id
                        if (candidato == null) candidato = id
                    }
                }
                return candidato
            }
        return null
    }

    private fun aEpochMillis(turno: Turno, fin: Boolean): Long {
        val fecha = if (fin) turno.fin else turno.inicio
        return fecha.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /** Crea el evento en el calendario elegido y devuelve su ID (para poder editarlo/borrarlo después). */
    suspend fun insertarEvento(context: Context, turno: Turno, paciente: Paciente): Long? {
        val calendarioId = calendarioDestinoId(context) ?: return null
        val valores = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarioId)
            put(CalendarContract.Events.TITLE, "${paciente.nombre} · ${turno.nombreTratamientoSnapshot}")
            put(CalendarContract.Events.DESCRIPTION, "Turno agendado en Luna y Armonía")
            put(CalendarContract.Events.DTSTART, aEpochMillis(turno, fin = false))
            put(CalendarContract.Events.DTEND, aEpochMillis(turno, fin = true))
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }
        val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, valores)
        return uri?.lastPathSegment?.toLongOrNull()
    }

    /** Actualiza fecha/hora del evento existente — se usa al reagendar. */
    fun actualizarEvento(context: Context, eventId: Long, turno: Turno) {
        val valores = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, aEpochMillis(turno, fin = false))
            put(CalendarContract.Events.DTEND, aEpochMillis(turno, fin = true))
        }
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        context.contentResolver.update(uri, valores, null, null)
    }

    /** Borra el evento — se usa al cancelar el turno. */
    fun borrarEvento(context: Context, eventId: Long) {
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        context.contentResolver.delete(uri, null, null)
    }
}
