package com.lunayarmonia.turnero.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PacienteDao {
    @Query("SELECT * FROM pacientes ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<Paciente>>

    @Query("SELECT * FROM pacientes WHERE nombre LIKE '%' || :busqueda || '%' ORDER BY nombre ASC")
    fun buscar(busqueda: String): Flow<List<Paciente>>

    @Query("SELECT * FROM pacientes WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Paciente?

    @Insert
    suspend fun insertar(paciente: Paciente): Long

    @Update
    suspend fun actualizar(paciente: Paciente)

    @Delete
    suspend fun borrar(paciente: Paciente)
}
