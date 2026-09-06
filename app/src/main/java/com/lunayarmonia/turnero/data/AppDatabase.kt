package com.lunayarmonia.turnero.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Paciente::class, Tratamiento::class, Turno::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pacienteDao(): PacienteDao
    abstract fun tratamientoDao(): TratamientoDao
    abstract fun turnoDao(): TurnoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "turnero.db"
                ).build().also { db ->
                    INSTANCE = db
                    // Carga el catálogo de tratamientos habituales la primera vez que se abre la app.
                    CoroutineScope(Dispatchers.IO).launch {
                        if (db.tratamientoDao().contar() == 0) {
                            db.tratamientoDao().insertarTodos(TRATAMIENTOS_INICIALES)
                        }
                    }
                }
            }
    }
}
