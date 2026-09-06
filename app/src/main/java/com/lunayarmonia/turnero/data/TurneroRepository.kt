package com.lunayarmonia.turnero.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Punto único de acceso a los datos. Hoy habla con Room (local). El día que
 * se quiera sync en la nube con el resto del equipo, esta es la clase que
 * se reemplaza/extiende para escribir también a Firestore — la UI y los
 * ViewModels no se enteran del cambio porque solo conocen esta interfaz.
 */
class TurneroRepository(private val db: AppDatabase) {

    // ---- Pacientes ----
    fun observarPacientes(): Flow<List<Paciente>> = db.pacienteDao().observarTodos()
    fun buscarPacientes(texto: String): Flow<List<Paciente>> = db.pacienteDao().buscar(texto)
    suspend fun obtenerPaciente(id: Long) = db.pacienteDao().obtenerPorId(id)
    suspend fun guardarPaciente(paciente: Paciente): Long =
        if (paciente.id == 0L) db.pacienteDao().insertar(paciente)
        else { db.pacienteDao().actualizar(paciente); paciente.id }

    // ---- Tratamientos ----
    fun observarTratamientos(): Flow<List<Tratamiento>> = db.tratamientoDao().observarActivos()
    suspend fun guardarTratamiento(tratamiento: Tratamiento): Long =
        if (tratamiento.id == 0L) db.tratamientoDao().insertar(tratamiento)
        else { db.tratamientoDao().actualizar(tratamiento); tratamiento.id }
    suspend fun borrarTratamiento(id: Long) = db.tratamientoDao().desactivar(id)

    // ---- Turnos ----
    fun observarTurnosDelDia(dia: LocalDateTime): Flow<List<Turno>> {
        val desde = dia.toLocalDate().atStartOfDay()
        val hasta = desde.plusDays(1)
        return db.turnoDao().observarPorRangoDeFecha(desde, hasta)
    }

    /** Rango genérico — lo usa la vista mensual de la Agenda para saber qué días tienen turnos. */
    fun observarTurnosEnRango(desde: LocalDateTime, hasta: LocalDateTime): Flow<List<Turno>> =
        db.turnoDao().observarPorRangoDeFecha(desde, hasta)

    fun observarTurnosDePaciente(pacienteId: Long): Flow<List<Turno>> =
        db.turnoDao().observarPorPaciente(pacienteId)

    suspend fun crearTurno(turno: Turno): Long = db.turnoDao().insertar(turno)

    /** Update genérico — hoy lo usa la sincronización con el calendario personal para guardar el calendarEventId. */
    suspend fun actualizarTurno(turno: Turno) = db.turnoDao().actualizar(turno)

    suspend fun reagendarTurno(turno: Turno, nuevoInicio: LocalDateTime): Turno {
        val actualizado = turno.copy(inicio = nuevoInicio, recordatorioEnviado = false)
        db.turnoDao().actualizar(actualizado)
        return actualizado
    }

    suspend fun cancelarTurno(turno: Turno) =
        db.turnoDao().actualizar(turno.copy(estado = EstadoTurno.CANCELADO))

    suspend fun marcarPagado(turno: Turno) =
        db.turnoDao().actualizar(turno.copy(estado = EstadoTurno.PAGADO))

    suspend fun marcarPendiente(turno: Turno) =
        db.turnoDao().actualizar(turno.copy(estado = EstadoTurno.PENDIENTE_DE_PAGO))

    suspend fun marcarNoShow(turno: Turno) =
        db.turnoDao().actualizar(turno.copy(estado = EstadoTurno.NO_SHOW))

    suspend fun borrarTurno(turno: Turno) = db.turnoDao().borrar(turno)

    /**
     * Se llama al abrir la Agenda: busca turnos cuya hora ya pasó y que
     * siguen en AGENDADO (no se marcaron pagados por adelantado), y los
     * devuelve para que la UI le pregunte a la usuaria "¿se cobró?".
     * No los cambia de estado sola — eso lo decide quien use la app.
     */
    suspend fun turnosQuePreguntarSiSeCobraron(ahora: LocalDateTime = LocalDateTime.now()): List<Turno> =
        db.turnoDao().obtenerVencidosSinResolver(ahora)

    // ---- Balance ----
    data class Balance(val cobrado: Double, val pendiente: Double) {
        val total: Double get() = cobrado + pendiente
    }

    suspend fun calcularBalance(desde: LocalDateTime, hasta: LocalDateTime): Balance {
        val pagados = db.turnoDao().obtenerPagadosEnRango(desde, hasta)
        val pendientes = db.turnoDao().obtenerPendientesEnRango(desde, hasta)
        return Balance(
            cobrado = pagados.sumOf { it.precioSnapshot },
            pendiente = pendientes.sumOf { it.precioSnapshot }
        )
    }
}
