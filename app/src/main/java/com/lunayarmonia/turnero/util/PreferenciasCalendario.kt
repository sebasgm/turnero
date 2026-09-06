package com.lunayarmonia.turnero.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "turnero_prefs")

private val CALENDARIO_ID = longPreferencesKey("calendario_id")
private val CALENDARIO_EMAIL = stringPreferencesKey("calendario_email")
private val CALENDARIO_NOMBRE = stringPreferencesKey("calendario_nombre")

data class CalendarioElegido(val id: Long, val email: String, val nombre: String)

/**
 * Guarda cuál calendario de Google eligió la usuaria en la pantalla Perfil
 * para que ahí se repliquen los turnos (cuando activa "Agregar a mi
 * calendario"). Sin esta elección explícita, CalendarSync cae de vuelta al
 * calendario primario del dispositivo — pero si hay más de una cuenta de
 * Google, eso puede no ser el que la usuaria espera.
 */
object PreferenciasCalendario {

    suspend fun guardar(context: Context, calendario: CalendarioElegido) {
        context.dataStore.edit { prefs ->
            prefs[CALENDARIO_ID] = calendario.id
            prefs[CALENDARIO_EMAIL] = calendario.email
            prefs[CALENDARIO_NOMBRE] = calendario.nombre
        }
    }

    fun observar(context: Context): Flow<CalendarioElegido?> =
        context.dataStore.data.map { prefs ->
            val id = prefs[CALENDARIO_ID] ?: return@map null
            val email = prefs[CALENDARIO_EMAIL] ?: return@map null
            val nombre = prefs[CALENDARIO_NOMBRE] ?: ""
            CalendarioElegido(id, email, nombre)
        }

    suspend fun obtenerIdGuardado(context: Context): Long? =
        context.dataStore.data.map { it[CALENDARIO_ID] }.first()
}
