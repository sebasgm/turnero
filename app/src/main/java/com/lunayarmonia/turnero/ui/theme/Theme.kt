package com.lunayarmonia.turnero.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = RosaMarca,
    secondary = VerdePagado
)

private val EsquemaOscuro = darkColorScheme(
    primary = RosaMarca,
    secondary = VerdePagado
)

@Composable
fun TurneroTheme(content: @Composable () -> Unit) {
    val esquema = if (isSystemInDarkTheme()) EsquemaOscuro else EsquemaClaro
    MaterialTheme(colorScheme = esquema, content = content)
}
