package cl.duoc.gestionprobadores.grupo9.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.duoc.gestionprobadores.grupo9.viewmodel.ColaViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.ProbadorViewModel

private enum class PantallaPrincipal {
    PANEL,
    COLA
}

@Composable
fun NavegacionPrincipal(
    probadorViewModel: ProbadorViewModel,
    colaViewModel: ColaViewModel
) {
    var pantallaActual by rememberSaveable {
        mutableStateOf(PantallaPrincipal.PANEL)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pantallaActual == PantallaPrincipal.PANEL,
                    onClick = {
                        pantallaActual = PantallaPrincipal.PANEL
                    },
                    icon = {
                        Text(text = "P")
                    },
                    label = {
                        Text(text = "Panel")
                    }
                )

                NavigationBarItem(
                    selected = pantallaActual == PantallaPrincipal.COLA,
                    onClick = {
                        pantallaActual = PantallaPrincipal.COLA
                    },
                    icon = {
                        Text(text = "C")
                    },
                    label = {
                        Text(text = "Cola")
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (pantallaActual) {
                PantallaPrincipal.PANEL -> {
                    PanelProbadoresScreen(
                        probadores = probadorViewModel.probadores
                    )
                }

                PantallaPrincipal.COLA -> {
                    ColaScreen(
                        viewModel = colaViewModel
                    )
                }
            }
        }
    }
}