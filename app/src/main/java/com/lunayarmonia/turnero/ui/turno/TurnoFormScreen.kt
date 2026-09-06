package com.lunayarmonia.turnero.ui.turno

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.lunayarmonia.turnero.data.Paciente
import com.lunayarmonia.turnero.data.Tratamiento
import com.lunayarmonia.turnero.data.Turno
import com.lunayarmonia.turnero.ui.AppViewModel
import com.lunayarmonia.turnero.util.compartirTurnoComoIcs
import com.lunayarmonia.turnero.util.formatearMoneda
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** turnoAReagendar == null -> modo "nuevo turno"; si viene cargado -> modo "reagendar". */
@Composable
fun TurnoFormScreen(
    vm: AppViewModel,
    turnoAReagendar: Turno?,
    onListo: () -> Unit
) {
    val context = LocalContext.current
    val pacientes by vm.pacientes.collectAsState()
    val tratamientos by vm.tratamientos.collectAsState()

    var fecha by remember { mutableStateOf((turnoAReagendar?.inicio ?: LocalDateTime.now()).toLocalDate()) }
    var hora by remember { mutableStateOf((turnoAReagendar?.inicio ?: LocalDateTime.now()).toLocalTime()) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text(
            if (turnoAReagendar == null) "Nuevo turno" else "Reagendar turno",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(16.dp))

        if (turnoAReagendar != null) {
            val paciente = pacientes.find { it.id == turnoAReagendar.pacienteId }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(paciente?.nombre ?: "Paciente", fontWeight = FontWeight.Medium)
                    Text(turnoAReagendar.nombreTratamientoSnapshot, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "Turno original: ${turnoAReagendar.inicio.format(DateTimeFormatter.ofPattern("EEE d MMM, HH:mm", Locale("es", "AR")))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            SelectorFechaHora(fecha, hora,
                onFecha = { fecha = it }, onHora = { hora = it }, context = context)

            var avisarWhatsApp by remember { mutableStateOf(true) }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = avisarWhatsApp, onCheckedChange = { avisarWhatsApp = it })
                Text("Avisar por WhatsApp del cambio")
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.reagendar(context, turnoAReagendar, LocalDateTime.of(fecha, hora))
                    // El aviso real se dispara desde la pantalla de detalle tras reagendar,
                    // reutilizando enviarRecordatorioWhatsApp con la nueva fecha/hora.
                    onListo()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Confirmar reagendo") }

        } else {
            var pacienteSeleccionado by remember { mutableStateOf<Paciente?>(null) }
            var nombreNuevoPaciente by remember { mutableStateOf("") }
            var telefonoNuevoPaciente by remember { mutableStateOf("") }
            var emailNuevoPaciente by remember { mutableStateOf("") }
            var tratamientoSeleccionado by remember { mutableStateOf<Tratamiento?>(null) }
            var precio by remember { mutableStateOf("") }
            var duracion by remember { mutableStateOf("") }
            var pagadoAhora by remember { mutableStateOf(false) }
            var notas by remember { mutableStateOf("") }
            var expandidoPaciente by remember { mutableStateOf(false) }
            var expandidoTratamiento by remember { mutableStateOf(false) }
            var agregarAlCalendario by remember { mutableStateOf(false) }
            var compartirAhora by remember { mutableStateOf(false) }
            var mostrarConfirmacion by remember { mutableStateOf(false) }

            val permisoCalendarioConcedido = ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED

            val pedirPermisoCalendario = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultados ->
                val concedido = resultados[Manifest.permission.WRITE_CALENDAR] == true
                agregarAlCalendario = concedido
                // Si dice que no, el turno se guarda igual — el calendario personal es opcional.
            }

            Text("Paciente", style = MaterialTheme.typography.labelMedium)
            ExposedDropdownMenuBox(expanded = expandidoPaciente, onExpandedChange = { expandidoPaciente = it }) {
                OutlinedTextField(
                    value = pacienteSeleccionado?.nombre ?: nombreNuevoPaciente,
                    onValueChange = { nombreNuevoPaciente = it; pacienteSeleccionado = null },
                    placeholder = { Text("Buscar o crear paciente") },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandidoPaciente, onDismissRequest = { expandidoPaciente = false }) {
                    pacientes.filter { it.nombre.contains(nombreNuevoPaciente, ignoreCase = true) }
                        .forEach { p ->
                            DropdownMenuItem(text = { Text(p.nombre) }, onClick = {
                                pacienteSeleccionado = p
                                expandidoPaciente = false
                            })
                        }
                }
            }
            if (pacienteSeleccionado == null && nombreNuevoPaciente.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = telefonoNuevoPaciente, onValueChange = { telefonoNuevoPaciente = it },
                    placeholder = { Text("+54 9 11 5555-1234") }, modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Incluí código de país y de área — se usa para el link de WhatsApp.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = emailNuevoPaciente, onValueChange = { emailNuevoPaciente = it },
                    placeholder = { Text("Email (opcional)") }, modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("Tratamiento", style = MaterialTheme.typography.labelMedium)
            ExposedDropdownMenuBox(expanded = expandidoTratamiento, onExpandedChange = { expandidoTratamiento = it }) {
                OutlinedTextField(
                    value = tratamientoSeleccionado?.let { "${it.nombre} · ${it.duracionMinutos} min" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Elegir tratamiento") },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandidoTratamiento, onDismissRequest = { expandidoTratamiento = false }) {
                    tratamientos.forEach { t ->
                        DropdownMenuItem(text = { Text("${t.nombre} · ${t.duracionMinutos} min") }, onClick = {
                            tratamientoSeleccionado = t
                            precio = t.precio.toInt().toString()
                            duracion = t.duracionMinutos.toString()
                            expandidoTratamiento = false
                        })
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = precio, onValueChange = { precio = it },
                    label = { Text("Precio") }, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = duracion, onValueChange = { duracion = it },
                    label = { Text("Duración (min)") }, modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
            SelectorFechaHora(fecha, hora, onFecha = { fecha = it }, onHora = { hora = it }, context = context)

            Spacer(Modifier.height(12.dp))
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = pagadoAhora, onCheckedChange = { pagadoAhora = it })
                    Text("Ya pagó este tratamiento")
                }
            }

            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(
                        checked = agregarAlCalendario,
                        onCheckedChange = { activar ->
                            if (!activar) {
                                agregarAlCalendario = false
                            } else if (permisoCalendarioConcedido) {
                                agregarAlCalendario = true
                            } else {
                                pedirPermisoCalendario.launch(
                                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                                )
                            }
                        }
                    )
                    Column {
                        Text("Agregar a mi calendario")
                        Text(
                            "Se crea un evento en tu Google Calendar personal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = notas, onValueChange = { notas = it },
                label = { Text("Notas (opcional)") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = compartirAhora, onCheckedChange = { compartirAhora = it })
                    Column {
                        Text("Compartir con la paciente ahora")
                        Text(
                            "Al guardar, se abre el turno para mandárselo por WhatsApp/mail",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                enabled = tratamientoSeleccionado != null &&
                    (pacienteSeleccionado != null || nombreNuevoPaciente.isNotBlank()),
                onClick = { mostrarConfirmacion = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar turno") }

            if (mostrarConfirmacion) {
                val tratamiento = tratamientoSeleccionado
                if (tratamiento != null) {
                    AlertDialog(
                        onDismissRequest = { mostrarConfirmacion = false },
                        title = { Text("Confirmar turno") },
                        text = {
                            Column {
                                Text(pacienteSeleccionado?.nombre ?: nombreNuevoPaciente, fontWeight = FontWeight.Medium)
                                Text("${tratamiento.nombre} · ${duracion.ifBlank { tratamiento.duracionMinutos.toString() }} min")
                                Text(
                                    "${fecha.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale("es", "AR")))}, " +
                                        hora.format(DateTimeFormatter.ofPattern("HH:mm"))
                                )
                                Text(formatearMoneda(precio.toDoubleOrNull() ?: tratamiento.precio))
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                mostrarConfirmacion = false
                                val inicio = LocalDateTime.of(fecha, hora)

                                fun crearElTurno(pacienteId: Long) {
                                    vm.crearTurno(
                                        context = context,
                                        pacienteId = pacienteId,
                                        tratamiento = tratamiento.copy(
                                            precio = precio.toDoubleOrNull() ?: tratamiento.precio,
                                            duracionMinutos = duracion.toIntOrNull() ?: tratamiento.duracionMinutos
                                        ),
                                        inicio = inicio,
                                        pagadoAhora = pagadoAhora,
                                        notas = notas.ifBlank { null },
                                        agregarAlCalendario = agregarAlCalendario,
                                        alCreado = { turnoCreado, pacienteCreado ->
                                            if (compartirAhora) {
                                                compartirTurnoComoIcs(context, turnoCreado, pacienteCreado)
                                            }
                                        }
                                    )
                                    onListo()
                                }

                                val existente = pacienteSeleccionado
                                if (existente != null) {
                                    crearElTurno(existente.id)
                                } else {
                                    vm.guardarPaciente(
                                        Paciente(
                                            nombre = nombreNuevoPaciente,
                                            telefono = telefonoNuevoPaciente,
                                            email = emailNuevoPaciente.ifBlank { null }
                                        )
                                    ) { nuevoId -> crearElTurno(nuevoId) }
                                }
                            }) { Text("Confirmar y guardar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectorFechaHora(
    fecha: LocalDate,
    hora: LocalTime,
    onFecha: (LocalDate) -> Unit,
    onHora: (LocalTime) -> Unit,
    context: android.content.Context
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = {
                DatePickerDialog(context, { _, y, m, d -> onFecha(LocalDate.of(y, m + 1, d)) },
                    fecha.year, fecha.monthValue - 1, fecha.dayOfMonth).show()
            },
            modifier = Modifier.weight(1f)
        ) { Text(fecha.format(DateTimeFormatter.ofPattern("d MMM", Locale("es", "AR")))) }

        OutlinedButton(
            onClick = {
                TimePickerDialog(context, { _, h, min -> onHora(LocalTime.of(h, min)) },
                    hora.hour, hora.minute, true).show()
            },
            modifier = Modifier.weight(1f)
        ) { Text(hora.format(DateTimeFormatter.ofPattern("HH:mm"))) }
    }
}
