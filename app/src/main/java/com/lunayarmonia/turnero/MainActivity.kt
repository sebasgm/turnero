package com.lunayarmonia.turnero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.lunayarmonia.turnero.data.AppDatabase
import com.lunayarmonia.turnero.data.TurneroRepository
import com.lunayarmonia.turnero.ui.AppViewModel
import com.lunayarmonia.turnero.ui.TurneroApp
import com.lunayarmonia.turnero.ui.theme.TurneroTheme

class MainActivity : ComponentActivity() {

    // DI manual y simple: no hace falta Hilt para el tamaño de esta app.
    // Si el día de mañana se agrega sync con Firestore, este es el único
    // lugar donde se arma el repositorio — el resto de la app no cambia.
    private val repositorio by lazy { TurneroRepository(AppDatabase.obtener(applicationContext)) }
    private val viewModel: AppViewModel by viewModels { AppViewModel.Factory(repositorio) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TurneroTheme {
                TurneroApp(vm = viewModel)
            }
        }
    }
}
