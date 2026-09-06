package com.lunayarmonia.turnero.data

import androidx.room.TypeConverter
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? = date?.toString()

    @TypeConverter
    fun fromEstadoTurno(value: EstadoTurno): String = value.name

    @TypeConverter
    fun toEstadoTurno(value: String): EstadoTurno = EstadoTurno.valueOf(value)

    @TypeConverter
    fun fromCategoria(value: CategoriaTratamiento): String = value.name

    @TypeConverter
    fun toCategoria(value: String): CategoriaTratamiento = CategoriaTratamiento.valueOf(value)
}
