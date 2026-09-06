package com.lunayarmonia.turnero.ui.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lunayarmonia.turnero.data.EstadoTurno
import com.lunayarmonia.turnero.data.Turno
import com.lunayarmonia.turnero.ui.AppViewModel
import com.lunayarmonia.turnero.ui.theme.*
import com.lunayarmonia.turnero.util.formatearMoneda
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private enum class VistaAgenda { DIA, MES }

@Composable
fun AgendaScreen(
    vm: AppViewModel,
    onNuevoTurno: () -> Unit,
    onAbrirTurno: (Long) -> Unit
) {
    val dia by vm.diaSeleccionado.collectAsState()
    val turnos by vm.turnosDelDia.collectAsState()
    val turnosDelMes by vm.turnosDelMes.collectAsState()
    val pacientes by vm.pacientes.collectAsState()
    val turnosAPreguntar by vm.turnosAPreguntar.collectAsState()
    var vistaAgenda by remember { mutableStateOf(VistaAgenda.DIA) }

    LaunchedEffect(Unit) { vm.revisarTurnosVencidos() }

    // Uno a la vez: si hay varios turnos vencidos sin resolver, se pregunta de a uno.
    turnosAPreguntar.firstOrNull()?.let { turno ->
        val paciente = pacientes.find { it.id == turno.pacienteId }
        DialogoSeCobro(
            nombrePaciente = paciente?.nombre ?: "la paciente",
            onPagado = { vm.marcarPagado(turno) },
            onPendiente = { vm.marcarPendiente(turno) },
            onNoShow = { vm.marcarNoShow(turno) }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            ToggleDiaMes(vistaAgenda, onCambiar = { vistaAgenda = it })
        }
        Spacer(Modifier.height(12.dp))

        if (vistaAgenda == VistaAgenda.MES) {
            VistaMensual(
                dia = dia,
                turnos = turnosDelMes,
                onElegirDia = { fecha ->
                    vm.irAlDia(fecha)
                    vistaAgenda = VistaAgenda.DIA
                }
            )
        } else {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { vm.cambiarDia(-1) }) { Icon(Icons.Default.ChevronLeft, "Día anterior") }
                Text(
                    dia.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "AR")))
                        .replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { vm.cambiarDia(1) }) { Icon(Icons.Default.ChevronRight, "Día siguiente") }
            }

            Spacer(Modifier.height(12.dp))

            if (turnos.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay turnos agendados este día.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(turnos, key = { it.id }) { turno ->
                        val nombrePaciente = pacientes.find { it.id == turno.pacienteId }?.nombre ?: "Paciente"
                        TarjetaTurno(turno, nombrePaciente, onClick = { onAbrirTurno(turno.id) })
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(onClick = onNuevoTurno, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Nuevo turno")
            }
        }
    }
}

@Composable
private fun ToggleDiaMes(actual: VistaAgenda, onCambiar: (VistaAgenda) -> Unit) {
    Row(
        Modifier
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        listOf(VistaAgenda.DIA to "Día", VistaAgenda.MES to "Mes").forEach { (v, etiqueta) ->
            val seleccionado = actual == v
            Text(
                etiqueta,
                modifier = Modifier
                    .background(if (seleccionado) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { onCambiar(v) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Calendario mensual simple: un puntito en los días que tienen turnos (sin contar cancelados). */
@Composable
private fun VistaMensual(
    dia: java.time.LocalDateTime,
    turnos: List<Turno>,
    onElegirDia: (java.time.LocalDateTime) -> Unit
) {
    val yearMonth = YearMonth.of(dia.year, dia.month)
    val primerDia = yearMonth.atDay(1)
    // Lunes = 0 ... Domingo = 6
    val offset = (primerDia.dayOfWeek.value + 6) % 7
    val diasEnMes = yearMonth.lengthOfMonth()
    val hoy = LocalDate.now()

    val diasConTurno = remember(turnos, yearMonth) {
        turnos.filter { it.estado != EstadoTurno.CANCELADO && YearMonth.from(it.inicio.toLocalDate()) == yearMonth }
            .map { it.inicio.dayOfMonth }
            .toSet()
    }

    Column {
        Text(
            yearMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "AR")).replaceFirstChar { it.uppercase() } + " ${yearMonth.year}",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(Modifier.fillMaxWidth()) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        val celdas = buildList {
            repeat(offset) { add(null) }
            for (d in 1..diasEnMes) add(d)
        }
        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.heightIn(max = 320.dp)) {
            items(celdas) { d ->
                if (d == null) {
                    Box(Modifier.aspectRatio(1f))
                } else {
                    val esHoy = LocalDate.of(yearMonth.year, yearMonth.month, d) == hoy
                    Column(
                        Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .then(if (esHoy) Modifier.background(MaterialTheme.colorScheme.primaryContainer) else Modifier)
                            .clickable { onElegirDia(dia.withDayOfMonth(d)) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(d.toString(), style = MaterialTheme.typography.bodySmall)
                        Box(
                            Modifier
                                .size(4.dp)
                                .background(
                                    if (d in diasConTurno) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

private data class EstiloEstado(
    val colorBarra: androidx.compose.ui.graphics.Color,
    val colorFondoBadge: androidx.compose.ui.graphics.Color,
    val colorTextoBadge: androidx.compose.ui.graphics.Color,
    val etiqueta: String
)

private fun estiloDe(estado: EstadoTurno): EstiloEstado = when (estado) {
    EstadoTurno.PAGADO -> EstiloEstado(VerdePagado, VerdePagadoFondo, VerdePagadoTexto, "Pagado")
    EstadoTurno.PENDIENTE_DE_PAGO -> EstiloEstado(AmbarPendiente, AmbarPendienteFondo, AmbarPendienteTexto, "Pendiente de pago")
    EstadoTurno.NO_SHOW -> EstiloEstado(GrisNeutral, GrisNeutralFondo, GrisNeutral, "No show")
    EstadoTurno.CANCELADO -> EstiloEstado(GrisNeutral, GrisNeutralFondo, GrisNeutral, "Cancelado")
    EstadoTurno.AGENDADO -> EstiloEstado(GrisNeutral, GrisNeutralFondo, GrisNeutral, "Agendado")
}

@Composable
private fun TarjetaTurno(turno: Turno, nombrePaciente: String, onClick: () -> Unit) {
    val estilo = estiloDe(turno.estado)

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(4.dp).fillMaxHeight().background(estilo.colorBarra))
            Column(Modifier.padding(12.dp).weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "${turno.inicio.format(DateTimeFormatter.ofPattern("HH:mm"))} · $nombrePaciente",
                        fontWeight = FontWeight.Medium
                    )
                    Surface(color = estilo.colorFondoBadge, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            estilo.etiqueta,
                            color = estilo.colorTextoBadge,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    "${turno.nombreTratamientoSnapshot} · ${turno.duracionMinutos} min · ${formatearMoneda(turno.precioSnapshot)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Dialog a medida (no AlertDialog) para poder controlar el orden y la
 * jerarquía visual: la opción positiva bien destacada arriba, "pendiente"
 * como segunda opción clara, y "no vino" separado abajo — para que no
 * compitan visualmente entre sí como pasaba antes con confirm/dismiss.
 */
@Composable
private fun DialogoSeCobro(
    nombrePaciente: String,
    onPagado: () -> Unit,
    onPendiente: () -> Unit,
    onNoShow: () -> Unit
) {
    Dialog(onDismissRequest = { /* obliga a elegir una opción */ }) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(20.dp)) {
                Text("¿Se cobró el turno de $nombrePaciente?", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "El turno ya terminó. Contanos cómo quedó para actualizar el balance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                Button(onClick = onPagado, modifier = Modifier.fillMaxWidth()) {
                    Text("Sí, se cobró")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onPendiente, modifier = Modifier.fillMaxWidth()) {
                    Text("No, quedó pendiente")
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onNoShow, modifier = Modifier.fillMaxWidth()) {
                    Text("La paciente no vino", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
