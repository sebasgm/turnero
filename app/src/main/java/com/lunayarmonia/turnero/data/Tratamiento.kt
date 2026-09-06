package com.lunayarmonia.turnero.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CategoriaTratamiento { FACIAL, CORPORAL }

@Entity(tableName = "tratamientos")
data class Tratamiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoria: CategoriaTratamiento,
    val precio: Double,
    val duracionMinutos: Int,
    val activo: Boolean = true // permite "borrar" sin romper turnos históricos que lo referencian
)

/** Catálogo inicial sugerido — se carga una sola vez si la tabla está vacía. */
val TRATAMIENTOS_INICIALES = listOf(
    Tratamiento(nombre = "Limpieza facial profunda", categoria = CategoriaTratamiento.FACIAL, precio = 12000.0, duracionMinutos = 45),
    Tratamiento(nombre = "Punta de diamante", categoria = CategoriaTratamiento.FACIAL, precio = 13000.0, duracionMinutos = 40),
    Tratamiento(nombre = "Peeling químico", categoria = CategoriaTratamiento.FACIAL, precio = 14000.0, duracionMinutos = 30),
    Tratamiento(nombre = "Radiofrecuencia facial", categoria = CategoriaTratamiento.FACIAL, precio = 15000.0, duracionMinutos = 45),
    Tratamiento(nombre = "Dermapen / microneedling", categoria = CategoriaTratamiento.FACIAL, precio = 17000.0, duracionMinutos = 50),
    Tratamiento(nombre = "Hidratación con ácido hialurónico", categoria = CategoriaTratamiento.FACIAL, precio = 16000.0, duracionMinutos = 40),
    Tratamiento(nombre = "Criofrecuencia facial", categoria = CategoriaTratamiento.FACIAL, precio = 15500.0, duracionMinutos = 45),
    Tratamiento(nombre = "Radiofrecuencia corporal", categoria = CategoriaTratamiento.CORPORAL, precio = 18000.0, duracionMinutos = 60),
    Tratamiento(nombre = "Cavitación", categoria = CategoriaTratamiento.CORPORAL, precio = 16000.0, duracionMinutos = 50),
    Tratamiento(nombre = "Presoterapia", categoria = CategoriaTratamiento.CORPORAL, precio = 10000.0, duracionMinutos = 40),
    Tratamiento(nombre = "Masaje reductor/descontracturante", categoria = CategoriaTratamiento.CORPORAL, precio = 14000.0, duracionMinutos = 50),
    Tratamiento(nombre = "Depilación láser/IPL", categoria = CategoriaTratamiento.CORPORAL, precio = 20000.0, duracionMinutos = 30),
    Tratamiento(nombre = "Mesoterapia", categoria = CategoriaTratamiento.CORPORAL, precio = 17000.0, duracionMinutos = 40),
    Tratamiento(nombre = "Vacumterapia", categoria = CategoriaTratamiento.CORPORAL, precio = 13000.0, duracionMinutos = 45),
    Tratamiento(nombre = "Electroestimulación", categoria = CategoriaTratamiento.CORPORAL, precio = 12000.0, duracionMinutos = 30)
)
