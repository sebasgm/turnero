package com.lunayarmonia.turnero.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Ciclo de vida de un turno, tal como quedó definido:
 *
 * AGENDADO --(se puede pagar en cualquier momento)--> PAGADO
 * AGENDADO --(termina la hora del turno, sin marcar pagado)--> se pregunta:
 *      "sí, se cobró"   -> PAGADO
 *      "no, pendiente"  -> PENDIENTE_DE_PAGO
 *      "no vino"        -> NO_SHOW
 * AGENDADO --(se cancela antes de que llegue la hora)--> CANCELADO
 *
 * NO_SHOW y CANCELADO son estados finales que no generan cargo distinto
 * (por ahora no hay seña ni penalidad).
 */
enum class EstadoTurno {
    AGENDADO,
    PENDIENTE_DE_PAGO,
    PAGADO,
    NO_SHOW,
    CANCELADO
}

@Entity(
    tableName = "turnos",
    foreignKeys = [
        ForeignKey(entity = Paciente::class, parentColumns = ["id"], childColumns = ["pacienteId"]),
        ForeignKey(entity = Tratamiento::class, parentColumns = ["id"], childColumns = ["tratamientoId"])
    ]
)
data class Turno(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pacienteId: Long,
    val tratamientoId: Long,
    val inicio: LocalDateTime,
    val duracionMinutos: Int,
    // El precio y el nombre del tratamiento se copian al crear el turno:
    // si más adelante cambiás precios en el catálogo, los turnos ya
    // agendados o pasados no se ven afectados retroactivamente.
    val nombreTratamientoSnapshot: String,
    val precioSnapshot: Double,
    val estado: EstadoTurno = EstadoTurno.AGENDADO,
    val notas: String? = null,
    val recordatorioEnviado: Boolean = false,
    // ID del evento en el Calendar Provider de Android (calendario personal
    // de quien da los turnos), si esta paciente/turno se sincronizó. Es
    // opcional y se define por turno, no automático para todos.
    val calendarEventId: Long? = null
) {
    val fin: LocalDateTime get() = inicio.plusMinutes(duracionMinutos.toLong())

    /** Un turno "cuenta como deuda" cuando ya pasó y todavía no se cobró. */
    val esDeudaPendiente: Boolean get() = estado == EstadoTurno.PENDIENTE_DE_PAGO
}
