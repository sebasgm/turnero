package com.lunayarmonia.turnero.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.lunayarmonia.turnero.data.Paciente
import com.lunayarmonia.turnero.data.Turno
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.UUID

private val FORMATO_ICS = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")

/** Nombre del centro tal como debe verse en el calendario de la paciente. */
private const val NOMBRE_CENTRO = "Luna y Armonía"

/**
 * Arma el contenido de un evento .ics para un turno. El título es
 * "Luna y Armonía - <tratamiento>": identifica el centro sin exponer
 * detalles si la paciente comparte pantalla con otra persona.
 */
fun generarIcs(turno: Turno): String {
    val uid = UUID.randomUUID().toString()
    val inicio = turno.inicio.format(FORMATO_ICS)
    val fin = turno.fin.format(FORMATO_ICS)
    val titulo = "$NOMBRE_CENTRO - ${turno.nombreTratamientoSnapshot}"

    return """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//$NOMBRE_CENTRO//Turnero//ES
        BEGIN:VEVENT
        UID:$uid
        DTSTAMP:${inicio}
        DTSTART:${inicio}
        DTEND:${fin}
        SUMMARY:$titulo
        LOCATION:$NOMBRE_CENTRO
        DESCRIPTION:Turno agendado en $NOMBRE_CENTRO.
        BEGIN:VALARM
        TRIGGER:-PT1H
        ACTION:DISPLAY
        DESCRIPTION:Recordatorio de turno
        END:VALARM
        END:VEVENT
        END:VCALENDAR
    """.trimIndent().replace("\n", "\r\n")
}

/**
 * Escribe el .ics en el cache de la app y abre el selector nativo de
 * Android para compartirlo (WhatsApp, mail, etc.). El archivo vive en
 * cache/turnos/ porque así lo declara file_paths.xml para el FileProvider.
 */
fun compartirTurnoComoIcs(context: Context, turno: Turno, paciente: Paciente) {
    val carpeta = File(context.cacheDir, "turnos").apply { mkdirs() }
    val nombreArchivo = "turno_${paciente.nombre.replace(" ", "_").lowercase()}.ics"
    val archivo = File(carpeta, nombreArchivo)
    archivo.writeText(generarIcs(turno))

    val uri = FileProvider.getUriForFile(context, "com.lunayarmonia.turnero.fileprovider", archivo)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/calendar"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir turno"))
}
