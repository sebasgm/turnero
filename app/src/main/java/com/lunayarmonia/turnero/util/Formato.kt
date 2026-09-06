package com.lunayarmonia.turnero.util

import java.text.NumberFormat
import java.util.Locale

private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "AR")).apply {
    maximumFractionDigits = 0
}

fun formatearMoneda(valor: Double): String = formatoMoneda.format(valor)
