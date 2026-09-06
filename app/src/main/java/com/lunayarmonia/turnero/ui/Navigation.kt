package com.lunayarmonia.turnero.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lunayarmonia.turnero.ui.agenda.AgendaScreen
import com.lunayarmonia.turnero.ui.balance.BalanceScreen
import com.lunayarmonia.turnero.ui.paciente.PacienteScreen
import com.lunayarmonia.turnero.ui.paciente.PacientesListScreen
import com.lunayarmonia.turnero.ui.perfil.PerfilScreen
import com.lunayarmonia.turnero.ui.tratamientos.TratamientosScreen
import com.lunayarmonia.turnero.ui.turno.TurnoDetailScreen
import com.lunayarmonia.turnero.ui.turno.TurnoFormScreen

private object Rutas {
    const val AGENDA = "agenda"
    const val PACIENTES = "pacientes"
    const val TRATAMIENTOS = "tratamientos"
    const val BALANCE = "balance"
    const val PERFIL = "perfil"
    const val TURNO_NUEVO = "turno_nuevo"
    const val TURNO_DETALLE = "turno_detalle/{turnoId}"
    const val TURNO_REAGENDAR = "turno_reagendar/{turnoId}"
    const val PACIENTE = "paciente/{pacienteId}"
    fun turnoDetalle(id: Long) = "turno_detalle/$id"
    fun turnoReagendar(id: Long) = "turno_reagendar/$id"
    fun paciente(id: Long) = "paciente/$id"
}

@Composable
fun TurneroApp(vm: AppViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BarraInferior(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.AGENDA,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Rutas.AGENDA) {
                AgendaScreen(
                    vm = vm,
                    onNuevoTurno = { navController.navigate(Rutas.TURNO_NUEVO) },
                    onAbrirTurno = { id -> navController.navigate(Rutas.turnoDetalle(id)) }
                )
            }
            composable(Rutas.TRATAMIENTOS) { TratamientosScreen(vm) }
            composable(Rutas.PACIENTES) {
                PacientesListScreen(vm, onAbrirPaciente = { id -> navController.navigate(Rutas.paciente(id)) })
            }
            composable(Rutas.BALANCE) { BalanceScreen(vm) }
            composable(Rutas.PERFIL) { PerfilScreen() }
            composable(Rutas.TURNO_NUEVO) {
                TurnoFormScreen(vm, turnoAReagendar = null, onListo = { navController.popBackStack() })
            }
            composable(Rutas.TURNO_DETALLE) { entrada ->
                val turnoId = entrada.arguments?.getString("turnoId")?.toLongOrNull() ?: return@composable
                TurnoDetailScreen(
                    vm = vm,
                    turnoId = turnoId,
                    onReagendar = { turno -> navController.navigate(Rutas.turnoReagendar(turno.id)) },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.TURNO_REAGENDAR) { entrada ->
                val turnoId = entrada.arguments?.getString("turnoId")?.toLongOrNull() ?: return@composable
                val turnos by vm.turnosDelDia.collectAsState()
                val turno = turnos.find { it.id == turnoId }
                TurnoFormScreen(vm, turnoAReagendar = turno, onListo = { navController.popBackStack("agenda", false) })
            }
            composable(Rutas.PACIENTE) { entrada ->
                val pacienteId = entrada.arguments?.getString("pacienteId")?.toLongOrNull() ?: return@composable
                PacienteScreen(vm, pacienteId, onNuevoTurno = { navController.navigate(Rutas.TURNO_NUEVO) })
            }
        }
    }
}

@Composable
private fun BarraInferior(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = rutaActual == Rutas.AGENDA,
            onClick = { navController.navigate(Rutas.AGENDA) },
            icon = { Icon(Icons.Default.CalendarMonth, "Agenda") },
            label = { Text("Agenda") }
        )
        NavigationBarItem(
            selected = rutaActual == Rutas.PACIENTES,
            onClick = { navController.navigate(Rutas.PACIENTES) },
            icon = { Icon(Icons.Default.Groups, "Pacientes") },
            label = { Text("Pacientes") }
        )
        NavigationBarItem(
            selected = rutaActual == Rutas.TRATAMIENTOS,
            onClick = { navController.navigate(Rutas.TRATAMIENTOS) },
            icon = { Icon(Icons.Default.Spa, "Tratamientos") },
            label = { Text("Tratamientos") }
        )
        NavigationBarItem(
            selected = rutaActual == Rutas.BALANCE,
            onClick = { navController.navigate(Rutas.BALANCE) },
            icon = { Icon(Icons.Default.Payments, "Balance") },
            label = { Text("Balance") }
        )
        NavigationBarItem(
            selected = rutaActual == Rutas.PERFIL,
            onClick = { navController.navigate(Rutas.PERFIL) },
            icon = { Icon(Icons.Default.AccountCircle, "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
