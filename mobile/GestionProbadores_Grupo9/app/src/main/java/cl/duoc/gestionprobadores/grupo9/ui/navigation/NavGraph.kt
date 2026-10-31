package cl.duoc.gestionprobadores.grupo9.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.gestionprobadores.grupo9.GestionProbadoresApplication
import cl.duoc.gestionprobadores.grupo9.ui.screens.AlertasScreen
import cl.duoc.gestionprobadores.grupo9.ui.screens.ColaScreen
import cl.duoc.gestionprobadores.grupo9.ui.screens.ConfigScreen
import cl.duoc.gestionprobadores.grupo9.ui.screens.DetalleScreen
import cl.duoc.gestionprobadores.grupo9.ui.screens.HistorialScreen
import cl.duoc.gestionprobadores.grupo9.ui.screens.LoginScreen
import cl.duoc.gestionprobadores.grupo9.ui.screens.PanelScreen
import cl.duoc.gestionprobadores.grupo9.ui.screens.RegistroScreen
import cl.duoc.gestionprobadores.grupo9.viewmodel.AlertasViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.ColaViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.ConfigViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.HistorialViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.LoginViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.PanelViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.ReportePrendaViewModel

/**
 * Punto único de navegación de la app: arma el NavController, el NavHost
 * con las 8 rutas, y la barra de navegación inferior (solo visible en las
 * 5 pantallas principales, no en Login/Registro/Detalle).
 */
@Composable
fun GestionProbadoresApp(application: GestionProbadoresApplication) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val mostrarBarraInferior = DestinoPrincipal.entries.any { it.ruta == rutaActual }

    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar {
                    DestinoPrincipal.entries.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = {
                                if (rutaActual != destino.ruta) {
                                    navController.navigate(destino.ruta) {
                                        popUpTo(Rutas.PANEL) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(iconoPara(destino), contentDescription = destino.etiqueta) },
                            label = { androidx.compose.material3.Text(destino.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.LOGIN) {
                val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory(application))
                LoginScreen(
                    viewModel = viewModel,
                    onLoginExitoso = {
                        navController.navigate(Rutas.PANEL) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    },
                    onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
                )
            }

            composable(Rutas.REGISTRO) {
                val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory(application))
                RegistroScreen(
                    viewModel = viewModel,
                    onRegistroExitoso = {
                        navController.navigate(Rutas.PANEL) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    },
                    onVolverALogin = { navController.popBackStack() }
                )
            }

            composable(Rutas.PANEL) {
                val viewModel: PanelViewModel = viewModel(factory = PanelViewModel.factory(application))
                PanelScreen(
                    viewModel = viewModel,
                    onProbadorClick = { id -> navController.navigate(Rutas.detalle(id)) }
                )
            }

            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument("probadorId") { type = NavType.IntType })
            ) { entry ->
                val probadorId = entry.arguments?.getInt("probadorId") ?: return@composable
                val panelViewModel: PanelViewModel = viewModel(factory = PanelViewModel.factory(application))
                val reporteViewModel: ReportePrendaViewModel =
                    viewModel(factory = ReportePrendaViewModel.factory(application))
                DetalleScreen(
                    probadorId = probadorId,
                    panelViewModel = panelViewModel,
                    reporteViewModel = reporteViewModel,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.COLA) {
                val viewModel: ColaViewModel = viewModel(factory = ColaViewModel.factory(application))
                ColaScreen(viewModel = viewModel)
            }

            composable(Rutas.ALERTAS) {
                val viewModel: AlertasViewModel = viewModel(factory = AlertasViewModel.factory(application))
                AlertasScreen(viewModel = viewModel)
            }

            composable(Rutas.HISTORIAL) {
                val viewModel: HistorialViewModel = viewModel(factory = HistorialViewModel.factory(application))
                HistorialScreen(viewModel = viewModel)
            }

            composable(Rutas.CONFIG) {
                val viewModel: ConfigViewModel = viewModel(factory = ConfigViewModel.factory(application))
                ConfigScreen(
                    viewModel = viewModel,
                    onSesionCerrada = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}

private fun iconoPara(destino: DestinoPrincipal) = when (destino) {
    DestinoPrincipal.PANEL -> Icons.Default.People
    DestinoPrincipal.COLA -> Icons.Default.Checkroom
    DestinoPrincipal.ALERTAS -> Icons.Default.Notifications
    DestinoPrincipal.HISTORIAL -> Icons.Default.History
    DestinoPrincipal.CONFIG -> Icons.Default.Settings
}
