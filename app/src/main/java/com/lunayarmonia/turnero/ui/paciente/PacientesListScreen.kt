package com.lunayarmonia.turnero.ui.paciente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lunayarmonia.turnero.data.Paciente
import com.lunayarmonia.turnero.ui.AppViewModel

@Composable
fun PacientesListScreen(vm: AppViewModel, onAbrirPaciente: (Long) -> Unit) {
    val pacientes by vm.pacientes.collectAsState()
    var busqueda by remember { mutableStateOf("") }
    var mostrarAlta by remember { mutableStateOf(false) }

    val filtrados = pacientes.filter { it.nombre.contains(busqueda, ignoreCase = true) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Pacientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            IconButton(onClick = { mostrarAlta = true }) { Icon(Icons.Default.Add, "Nuevo paciente") }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar paciente") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(filtrados, key = { it.id }) { paciente ->
                FilaPaciente(paciente, onClick = { onAbrirPaciente(paciente.id) })
                HorizontalDivider()
            }
            if (filtrados.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No se encontraron pacientes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (mostrarAlta) {
        DialogoPaciente(
            onCancelar = { mostrarAlta = false },
            onGuardar = { nombre, telefono, email ->
                vm.guardarPaciente(Paciente(nombre = nombre, telefono = telefono, email = email.ifBlank { null })) {}
                mostrarAlta = false
            }
        )
    }
}

@Composable
private fun FilaPaciente(paciente: Paciente, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(androidx.compose.foundation.clickable(onClick = onClick))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    paciente.nombre.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(paciente.nombre, style = MaterialTheme.typography.bodyMedium)
                Text(paciente.telefono, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Alta o edición de paciente — no requiere crear ningún turno. */
@Composable
fun DialogoPaciente(
    inicial: Paciente? = null,
    onCancelar: () -> Unit,
    onGuardar: (nombre: String, telefono: String, email: String) -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var telefono by remember { mutableStateOf(inicial?.telefono ?: "") }
    var email by remember { mutableStateOf(inicial?.email ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (inicial == null) "Nuevo paciente" else "Editar paciente") },
        text = {
            Column {
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre y apellido") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    telefono, { telefono = it },
                    placeholder = { Text("+54 9 11 5555-1234") },
                    label = { Text("Teléfono") },
                    singleLine = true
                )
                Text(
                    "Incluí código de país y de área — se usa para el link de WhatsApp.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(email, { email = it }, label = { Text("Email (opcional)") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                enabled = nombre.isNotBlank() && telefono.isNotBlank(),
                onClick = { onGuardar(nombre, telefono, email) }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
