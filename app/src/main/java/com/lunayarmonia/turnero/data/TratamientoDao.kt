package com.lunayarmonia.turnero.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TratamientoDao {
    @Query("SELECT * FROM tratamientos WHERE activo = 1 ORDER BY categoria, nombre")
    fun observarActivos(): Flow<List<Tratamiento>>

    @Query("SELECT COUNT(*) FROM tratamientos")
    suspend fun contar(): Int

    @Insert
    suspend fun insertarTodos(tratamientos: List<Tratamiento>)

    @Insert
    suspend fun insertar(tratamiento: Tratamiento): Long

    @Update
    suspend fun actualizar(tratamiento: Tratamiento)

    // "Borrar" un tratamiento lo desactiva en lugar de eliminarlo:
    // los turnos ya creados guardan su propio snapshot de nombre/precio,
    // así que no se rompen, pero el tratamiento deja de aparecer para turnos nuevos.
    @Query("UPDATE tratamientos SET activo = 0 WHERE id = :id")
    suspend fun desactivar(id: Long)
}
