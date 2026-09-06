package com.lunayarmonia.turnero.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TurnoDao {

    @Query("SELECT * FROM turnos WHERE inicio >= :desde AND inicio < :hasta ORDER BY inicio ASC")
    fun observarPorRangoDeFecha(desde: LocalDateTime, hasta: LocalDateTime): Flow<List<Turno>>

    @Query("SELECT * FROM turnos WHERE pacienteId = :pacienteId ORDER BY inicio DESC")
    fun observarPorPaciente(pacienteId: Long): Flow<List<Turno>>

    @Query("SELECT * FROM turnos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Turno?

    // Turnos que ya terminaron y siguen en AGENDADO: son los candidatos a
    // los que hay que preguntarles "¿se cobró?" (ver TurnoRepository.revisarTurnosVencidos).
    @Query("SELECT * FROM turnos WHERE estado = 'AGENDADO' AND inicio < :ahora")
    suspend fun obtenerVencidosSinResolver(ahora: LocalDateTime): List<Turno>

    // Para el balance: turnos pagados dentro de un período (el período lo arma el ViewModel).
    @Query("SELECT * FROM turnos WHERE estado = 'PAGADO' AND inicio >= :desde AND inicio < :hasta")
    suspend fun obtenerPagadosEnRango(desde: LocalDateTime, hasta: LocalDateTime): List<Turno>

    @Query("SELECT * FROM turnos WHERE estado = 'PENDIENTE_DE_PAGO' AND inicio >= :desde AND inicio < :hasta")
    suspend fun obtenerPendientesEnRango(desde: LocalDateTime, hasta: LocalDateTime): List<Turno>

    @Insert
    suspend fun insertar(turno: Turno): Long

    @Update
    suspend fun actualizar(turno: Turno)

    @Delete
    suspend fun borrar(turno: Turno)
}
