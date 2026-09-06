package com.lunayarmonia.turnero.ui.tratamientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lunayarmonia.turnero.data.CategoriaTratamiento
import com.lunayarmonia.turnero.data.Tratamiento
import com.lunayarmonia.turnero.ui.AppViewModel
import com.lunayarmonia.turnero.util.formatearMoneda

@Composable
fun TratamientosScreen(vm: AppViewModel) {
    val tratamientos by vm.tratamientos.collectAsState()
    var mostrarAlta by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Tratamiento?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tratamientos", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { mostrarAlta = true }) { Icon(Icons.Default.Add, "Agregar tratamiento") }
        }

        LazyColumn {
            items(tratamientos, key = { it.id }) { t ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .then(androidx.compose.foundation.clickable(onClick = { editando = t }))
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(t.nombre)
                            Text(
                                "${if (t.categoria == CategoriaTratamiento.FACIAL) "Facial" else "Corporal"} · ${t.duracionMinutos} min",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(formatearMoneda(t.precio))
                        IconButton(onClick = { vm.borrarTratamiento(t.id) }) {
                            Icon(Icons.Default.Delete, "Quitar tratamiento")
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }

    if (mostrarAlta) {
        DialogoTratamiento(
            onCancelar = { mostrarAlta = false },
            onGuardar = { nombre, categoria, precio, duracion ->
                vm.guardarTratamiento(
                    Tratamiento(nombre = nombre, categoria = categoria, precio = precio, duracionMinutos = duracion)
                )
                mostrarAlta = false
            }
        )
    }

    editando?.let { tratamiento ->
        DialogoTratamiento(
            inicial = tratamiento,
            onCancelar = { editando = null },
            onGuardar = { nombre, categoria, precio, duracion ->
                vm.guardarTratamiento(tratamiento.copy(nombre = nombre, categoria = categoria, precio = precio, duracionMinutos = duracion))
                editando = null
            }
        )
    }
}

@Composable
private fun DialogoTratamiento(
    inicial: Tratamiento? = null,
    onCancelar: () -> Unit,
    onGuardar: (String, CategoriaTratamiento, Double, Int) -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var categoria by remember { mutableStateOf(inicial?.categoria ?: CategoriaTratamiento.FACIAL) }
    var precio by remember { mutableStateOf(inicial?.precio?.toInt()?.toString() ?: "") }
    var duracion by remember { mutableStateOf(inicial?.duracionMinutos?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (inicial == null) "Nuevo tratamiento" else "Editar tratamiento") },
        text = {
            Column {
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") })
                Spacer(Modifier.height(8.dp))
                Row {
                    FilterChip(
                        selected = categoria == CategoriaTratamiento.FACIAL,
                        onClick = { categoria = CategoriaTratamiento.FACIAL },
                        label = { Text("Facial") }
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = categoria == CategoriaTratamiento.CORPORAL,
                        onClick = { categoria = CategoriaTratamiento.CORPORAL },
                        label = { Text("Corporal") }
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(precio, { precio = it }, label = { Text("Precio") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(duracion, { duracion = it }, label = { Text("Duración (min)") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onGuardar(nombre, categoria, precio.toDoubleOrNull() ?: 0.0, duracion.toIntOrNull() ?: 30)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
