package com.lunayarmonia.turnero.ui.paciente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lunayarmonia.turnero.data.EstadoTurno
import com.lunayarmonia.turnero.data.Turno
import com.lunayarmonia.turnero.ui.AppViewModel
import com.lunayarmonia.turnero.util.formatearMoneda
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PacienteScreen(vm: AppViewModel, pacienteId: Long, onNuevoTurno: () -> Unit) {
    val pacientes by vm.pacientes.collectAsState()
    val paciente = pacientes.find { it.id == pacienteId } ?: return
    val turnos by vm.turnosDePaciente(pacienteId).collectAsState(initial = emptyList())

    val totalTratamientos = turnos.filter {
        it.estado == EstadoTurno.PAGADO || it.estado == EstadoTurno.PENDIENTE_DE_PAGO
    }.sumOf { it.precioSnapshot }
    val pendiente = turnos.filter { it.estado == EstadoTurno.PENDIENTE_DE_PAGO }.sumOf { it.precioSnapshot }
    val noShows = turnos.count { it.estado == EstadoTurno.NO_SHOW }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(paciente.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Text(paciente.telefono, style = MaterialTheme.typography.bodySmall)
        paciente.email?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Metrica("Turnos totales", turnos.size.toString(), Modifier.weight(1f))
            Metrica("Total tratamientos", formatearMoneda(totalTratamientos), Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Metrica("Pendiente de pago", formatearMoneda(pendiente), Modifier.weight(1f))
            Metrica("No shows", noShows.toString(), Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))
        Text("Historial", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(turnos, key = { it.id }) { turno -> FilaHistorial(turno) }
        }

        Button(onClick = onNuevoTurno, modifier = Modifier.fillMaxWidth()) {
            Text("Nuevo turno para ${paciente.nombre.substringBefore(" ")}")
        }
    }
}

@Composable
private fun Metrica(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(etiqueta, style = MaterialTheme.typography.labelSmall)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FilaHistorial(turno: Turno) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(turno.nombreTratamientoSnapshot, style = MaterialTheme.typography.bodyMedium)
            Text(
                turno.inicio.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale("es", "AR"))),
                style = MaterialTheme.typography.labelSmall
            )
        }
        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(
                turno.estado.name.lowercase().replace("_", " "),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
