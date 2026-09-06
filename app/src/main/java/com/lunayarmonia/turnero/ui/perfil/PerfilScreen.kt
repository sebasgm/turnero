package com.lunayarmonia.turnero.ui.perfil

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.lunayarmonia.turnero.util.CalendarSync
import com.lunayarmonia.turnero.util.CalendarioElegido
import com.lunayarmonia.turnero.util.PreferenciasCalendario
import kotlinx.coroutines.launch

/**
 * Acá se elige explícitamente en qué cuenta/calendario de Google se
 * replican los turnos cuando se activa "Agregar a mi calendario" al crear
 * uno. Si nunca se elige nada acá, CalendarSync sigue cayendo de vuelta al
 * calendario primario del dispositivo (comportamiento anterior).
 */
@Composable
fun PerfilScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val calendarioGuardado by PreferenciasCalendario.observar(context).collectAsState(initial = null)

    var permisoConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        )
    }
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        permisoConcedido = concedido
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Perfil", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Elegí en qué calendario de Google se agregan los turnos cuando activás \"Agregar a mi calendario\".",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        calendarioGuardado?.let { actual ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Calendario actual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(actual.nombre, fontWeight = FontWeight.Medium)
                    Text(actual.email, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (!permisoConcedido) {
            Text(
                "Para ver tus calendarios disponibles, la app necesita permiso de calendario.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { pedirPermiso.launch(Manifest.permission.READ_CALENDAR) }) {
                Text("Dar permiso")
            }
        } else {
            val calendarios = remember { CalendarSync.listarCalendariosDisponibles(context) }

            if (calendarios.isEmpty()) {
                Text(
                    "No se encontró ningún calendario editable en este dispositivo. Agregá una cuenta de Google en Ajustes del teléfono e volvé acá.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text("Calendarios disponibles", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                LazyColumn {
                    items(calendarios, key = { it.id }) { cal ->
                        val seleccionado = calendarioGuardado?.id == cal.id
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .then(
                                    androidx.compose.foundation.clickable {
                                        scope.launch {
                                            PreferenciasCalendario.guardar(
                                                context,
                                                CalendarioElegido(cal.id, cal.email, cal.nombre)
                                            )
                                        }
                                    }
                                )
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row {
                                    Text(cal.nombre, fontWeight = FontWeight.Medium)
                                    if (cal.esPrimario) {
                                        Spacer(Modifier.width(6.dp))
                                        Text("· primario", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Text(cal.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (seleccionado) {
                                Icon(Icons.Default.Check, contentDescription = "Elegido", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
