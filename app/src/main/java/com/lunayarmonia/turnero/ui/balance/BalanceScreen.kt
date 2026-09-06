package com.lunayarmonia.turnero.ui.balance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lunayarmonia.turnero.ui.AppViewModel
import com.lunayarmonia.turnero.ui.Periodo
import com.lunayarmonia.turnero.util.formatearMoneda

@Composable
fun BalanceScreen(vm: AppViewModel) {
    val periodo by vm.periodo.collectAsState()
    val balance by vm.balance.collectAsState()

    LaunchedEffect(Unit) { vm.recalcularBalance() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Periodo.HOY to "Hoy", Periodo.SEMANA to "Semana", Periodo.MES to "Mes").forEach { (p, etiqueta) ->
                FilterChip(
                    selected = periodo == p,
                    onClick = { vm.elegirPeriodo(p) },
                    label = { Text(etiqueta) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Total generado", style = MaterialTheme.typography.labelMedium)
                Text(
                    formatearMoneda(balance.total),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(Modifier.weight(1f)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Cobrado", style = MaterialTheme.typography.labelSmall)
                    Text(formatearMoneda(balance.cobrado), fontWeight = FontWeight.Medium)
                }
            }
            Card(Modifier.weight(1f)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Pendiente", style = MaterialTheme.typography.labelSmall)
                    Text(formatearMoneda(balance.pendiente), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
