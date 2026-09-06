package com.lunayarmonia.turnero.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lunayarmonia.turnero.data.*
import com.lunayarmonia.turnero.util.CalendarSync
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

enum class Periodo { HOY, SEMANA, MES }

class AppViewModel(private val repo: TurneroRepository) : ViewModel() {

    // ---- Agenda ----
    private val _diaSeleccionado = MutableStateFlow(LocalDateTime.now())
    val diaSeleccionado: StateFlow<LocalDateTime> = _diaSeleccionado

    @Suppress("OPT_IN_USAGE")
    val turnosDelDia: StateFlow<List<Turno>> = _diaSeleccionado
        .flatMapLatest { repo.observarTurnosDelDia(it) }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    // Todos los turnos del mes visible en la Agenda (para la vista de calendario mensual).
    @Suppress("OPT_IN_USAGE")
    val turnosDelMes: StateFlow<List<Turno>> = _diaSeleccionado
        .flatMapLatest { dia ->
            val desde = dia.toLocalDate().withDayOfMonth(1).atStartOfDay()
            val hasta = desde.plusMonths(1)
            repo.observarTurnosEnRango(desde, hasta)
        }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    val pacientes: StateFlow<List<Paciente>> = repo.observarPacientes()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    val tratamientos: StateFlow<List<Tratamiento>> = repo.observarTratamientos()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    // Turnos vencidos a los que hay que preguntarles "¿se cobró?" (ver flujo de estados)
    private val _turnosAPreguntar = MutableStateFlow<List<Turno>>(emptyList())
    val turnosAPreguntar: StateFlow<List<Turno>> = _turnosAPreguntar

    fun revisarTurnosVencidos() {
        viewModelScope.launch {
            _turnosAPreguntar.value = repo.turnosQuePreguntarSiSeCobraron()
        }
    }

    fun cambiarDia(delta: Long) {
        _diaSeleccionado.value = _diaSeleccionado.value.plusDays(delta)
    }

    /** Salta directo a una fecha — lo usa la vista mensual al tocar un día. */
    fun irAlDia(fecha: LocalDateTime) {
        _diaSeleccionado.value = fecha
    }

    // ---- Acciones sobre un turno ----

    /**
     * agregarAlCalendario es un toggle por turno (no automático): si viene
     * en true, además de guardar el turno en la app se crea un evento en
     * el calendario personal de quien da los turnos (Calendar Provider de
     * Android). El context se pide solo para esto — la app no lo necesita
     * para nada más de la lógica de datos.
     *
     * alCreado se dispara con el turno ya guardado (con su id real) y la
     * paciente correspondiente — lo usa la pantalla para abrir "Compartir"
     * de inmediato si se tildó esa opción en el formulario.
     */
    fun crearTurno(
        context: Context,
        pacienteId: Long,
        tratamiento: Tratamiento,
        inicio: LocalDateTime,
        pagadoAhora: Boolean,
        notas: String?,
        agregarAlCalendario: Boolean,
        alCreado: (Turno, Paciente) -> Unit = { _, _ -> }
    ) = viewModelScope.launch {
        val turno = Turno(
            pacienteId = pacienteId,
            tratamientoId = tratamiento.id,
            inicio = inicio,
            duracionMinutos = tratamiento.duracionMinutos,
            nombreTratamientoSnapshot = tratamiento.nombre,
            precioSnapshot = tratamiento.precio,
            estado = if (pagadoAhora) EstadoTurno.PAGADO else EstadoTurno.AGENDADO,
            notas = notas
        )
        val id = repo.crearTurno(turno)
        var turnoConId = turno.copy(id = id)

        if (agregarAlCalendario) {
            val paciente = repo.obtenerPaciente(pacienteId)
            if (paciente != null) {
                val eventId = CalendarSync.insertarEvento(context, turnoConId, paciente)
                if (eventId != null) {
                    turnoConId = turnoConId.copy(calendarEventId = eventId)
                    repo.actualizarTurno(turnoConId)
                }
            }
        }

        val paciente = repo.obtenerPaciente(pacienteId)
        if (paciente != null) alCreado(turnoConId, paciente)
    }

    fun reagendar(context: Context, turno: Turno, nuevoInicio: LocalDateTime) = viewModelScope.launch {
        val actualizado = repo.reagendarTurno(turno, nuevoInicio)
        turno.calendarEventId?.let { eventId -> CalendarSync.actualizarEvento(context, eventId, actualizado) }
    }

    fun cancelar(context: Context, turno: Turno) = viewModelScope.launch {
        repo.cancelarTurno(turno)
        turno.calendarEventId?.let { eventId -> CalendarSync.borrarEvento(context, eventId) }
    }
    fun marcarPagado(turno: Turno) = viewModelScope.launch {
        repo.marcarPagado(turno)
        _turnosAPreguntar.value = _turnosAPreguntar.value - turno
    }
    fun marcarPendiente(turno: Turno) = viewModelScope.launch {
        repo.marcarPendiente(turno)
        _turnosAPreguntar.value = _turnosAPreguntar.value - turno
    }
    fun marcarNoShow(turno: Turno) = viewModelScope.launch {
        repo.marcarNoShow(turno)
        _turnosAPreguntar.value = _turnosAPreguntar.value - turno
    }
    fun borrarTurno(context: Context, turno: Turno) = viewModelScope.launch {
        repo.borrarTurno(turno)
        turno.calendarEventId?.let { eventId -> CalendarSync.borrarEvento(context, eventId) }
    }

    // ---- Pacientes ----
    fun guardarPaciente(paciente: Paciente, alGuardar: (Long) -> Unit) = viewModelScope.launch {
        val id = repo.guardarPaciente(paciente)
        alGuardar(id)
    }

    fun turnosDePaciente(pacienteId: Long) = repo.observarTurnosDePaciente(pacienteId)

    // ---- Tratamientos ----
    fun guardarTratamiento(tratamiento: Tratamiento) = viewModelScope.launch {
        repo.guardarTratamiento(tratamiento)
    }
    fun borrarTratamiento(id: Long) = viewModelScope.launch { repo.borrarTratamiento(id) }

    // ---- Balance ----
    private val _periodo = MutableStateFlow(Periodo.HOY)
    val periodo: StateFlow<Periodo> = _periodo

    private val _balance = MutableStateFlow(TurneroRepository.Balance(0.0, 0.0))
    val balance: StateFlow<TurneroRepository.Balance> = _balance

    fun elegirPeriodo(p: Periodo) {
        _periodo.value = p
        recalcularBalance()
    }

    fun recalcularBalance() = viewModelScope.launch {
        val ahora = LocalDateTime.now()
        val hoyInicio = ahora.toLocalDate().atStartOfDay()
        val desde = when (_periodo.value) {
            Periodo.HOY -> hoyInicio
            Periodo.SEMANA -> hoyInicio.minusDays(hoyInicio.dayOfWeek.value - 1L)
            Periodo.MES -> hoyInicio.withDayOfMonth(1)
        }
        val hasta = hoyInicio.plusDays(1)
        _balance.value = repo.calcularBalance(desde, hasta)
    }

    class Factory(private val repo: TurneroRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repo) as T
        }
    }
}
