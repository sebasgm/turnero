package com.lunayarmonia.turnero.ui.turno

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lunayarmonia.turnero.data.Turno
import com.lunayarmonia.turnero.ui.AppViewModel
import com.lunayarmonia.turnero.util.compartirTurnoComoIcs
import com.lunayarmonia.turnero.util.enviarRecordatorioWhatsApp
import com.lunayarmonia.turnero.util.formatearMoneda
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TurnoDetailScreen(
    vm: AppViewModel,
    turnoId: Long,
    onReagendar: (Turno) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val pacientes by vm.pacientes.collectAsState()
    val turnosDelDia by vm.turnosDelDia.collectAsState()
    // En una app real esto vendría de un observarPorId; para no sumar otro Flow al
    // ViewModel de ejemplo, lo buscamos entre los turnos ya cargados del día actual.
    val turno = turnosDelDia.find { it.id == turnoId } ?: return
    val paciente = pacientes.find { it.id == turno.pacienteId } ?: return

    var mostrarConfirmarCancelacion by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(paciente.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Text(
            "${turno.nombreTratamientoSnapshot} · ${turno.inicio.format(DateTimeFormatter.ofPattern("EEE d MMM, HH:mm", Locale("es", "AR")))}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(formatearMoneda(turno.precioSnapshot), style = MaterialTheme.typography.bodyMedium)
        if (turno.calendarEventId != null) {
            Text(
                "En tu calendario personal",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onReagendar(turno) }, modifier = Modifier.weight(1f)) {
                Text("Reagendar")
            }
            OutlinedButton(onClick = { vm.marcarPagado(turno) }, modifier = Modifier.weight(1f)) {
                Text("Marcar pagado")
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { compartirTurnoComoIcs(context, turno, paciente) },
                modifier = Modifier.weight(1f)
            ) { Text("Compartir") }
            OutlinedButton(
                onClick = { mostrarConfirmarCancelacion = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Cancelar") }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { enviarRecordatorioWhatsApp(context, turno, paciente) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Enviar recordatorio WhatsApp") }
    }

    if (mostrarConfirmarCancelacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarCancelacion = false },
            title = { Text("¿Cancelar este turno?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.cancelar(context, turno)
                    mostrarConfirmarCancelacion = false
                    onVolver()
                }) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarCancelacion = false }) { Text("Volver") }
            }
        )
    }
}
