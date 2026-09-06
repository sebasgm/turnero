package com.lunayarmonia.turnero.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ficha de paciente. El email es opcional (no todas las pacientes lo tienen
 * a mano al momento de agendar), pero queda disponible para el día que se
 * sumen confirmaciones por mail o factura electrónica.
 */
@Entity(tableName = "pacientes")
data class Paciente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val telefono: String,
    val email: String? = null,
    val notas: String? = null
)
