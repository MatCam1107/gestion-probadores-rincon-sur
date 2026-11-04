package cl.duoc.gestionprobadores.grupo9.navegations

import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.gestionprobadores.grupo9.repository.models.Usuario
import cl.duoc.gestionprobadores.grupo9.view.ui.AlertasScreen
import cl.duoc.gestionprobadores.grupo9.view.ui.CuentaFormScreen
import cl.duoc.gestionprobadores.grupo9.view.ui.CuentasScreen
import cl.duoc.gestionprobadores.grupo9.view.ui.DetalleProbadorScreen
import cl.duoc.gestionprobadores.grupo9.view.ui.HistorialScreen
import cl.duoc.gestionprobadores.grupo9.view.ui.IncidenciasScreen
import cl.duoc.gestionprobadores.grupo9.view.ui.LoginScreen
import cl.duoc.gestionprobadores.grupo9.view.ui.PanelProbadoresScreen
import cl.duoc.gestionprobadores.grupo9.view.ui.ReporteIncidenciaScreen
import cl.duoc.gestionprobadores.grupo9.view.utils.navEnterTransition
import cl.duoc.gestionprobadores.grupo9.view.utils.navExitTransition
import cl.duoc.gestionprobadores.grupo9.view.utils.navPopEnterTransition
import cl.duoc.gestionprobadores.grupo9.view.utils.navPopExitTransition
import cl.duoc.gestionprobadores.grupo9.viewmodels.IncidenciaViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodels.LoginViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodels.ProbadorViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodels.UsuarioViewModel

object Routes {
    const val LOGIN = "login"
    const val PANEL = "panel"
    const val DETALLE = "detalle/{probadorId}"
    const val REPORTE = "reporte?probadorId={probadorId}"
    const val ALERTAS = "alertas"
    const val INCIDENCIAS = "incidencias"
    const val HISTORIAL = "historial"
    const val CUENTAS = "cuentas"
    const val CUENTA = "cuenta/{usuarioId}"

    fun detalle(id: Int) = "detalle/$id"
    fun reporte(id: Int = 0) = "reporte?probadorId=$id"
    fun cuenta(id: Int = 0) = "cuenta/$id"
}

private data class Destino(val route: String, val etiqueta: String, val icono: androidx.compose.ui.graphics.vector.ImageVector)

private val destinosColaborador = listOf(
    Destino(Routes.PANEL, "Panel", Icons.Default.Dashboard),
    Destino(Routes.reporte(), "Reportar", Icons.Default.ReportProblem),
    Destino(Routes.ALERTAS, "Alertas", Icons.Default.Notifications),
    Destino(Routes.HISTORIAL, "Historial", Icons.Default.History)
)

private val destinosAdministrador = listOf(
    Destino(Routes.PANEL, "Panel", Icons.Default.Dashboard),
    Destino(Routes.INCIDENCIAS, "Incidencias", Icons.Default.ReportProblem),
    Destino(Routes.ALERTAS, "Alertas", Icons.Default.Notifications),
    Destino(Routes.HISTORIAL, "Historial", Icons.Default.History),
    Destino(Routes.CUENTAS, "Cuentas", Icons.Default.AccountCircle)
)

private fun tituloRuta(route: String?): String = when (route) {
    Routes.PANEL -> "Vestuario"
    Routes.DETALLE -> "Detalle del probador"
    Routes.REPORTE -> "Reportar incidencia"
    Routes.ALERTAS -> "Alertas"
    Routes.INCIDENCIAS -> "Incidencias"
    Routes.HISTORIAL -> "Historial"
    Routes.CUENTAS -> "Cuentas"
    Routes.CUENTA -> "Cuenta"
    else -> ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    probadorViewModel: ProbadorViewModel,
    incidenciaViewModel: IncidenciaViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val navController = rememberNavController()
    val login by loginViewModel.uiState.collectAsStateWithLifecycle()
    val probadores by probadorViewModel.probadores.collectAsStateWithLifecycle()
    val usos by probadorViewModel.usos.collectAsStateWithLifecycle()
    val colas by probadorViewModel.colas.collectAsStateWithLifecycle()
    val ahora by probadorViewModel.ahora.collectAsStateWithLifecycle()
    val incidencias by incidenciaViewModel.incidencias.collectAsStateWithLifecycle()
    val usuarios by usuarioViewModel.usuarios.collectAsStateWithLifecycle()
    val entrada by navController.currentBackStackEntryAsState()
    val route = entrada?.destination?.route
    val usuario = login.usuario
    val esLogin = route == null || route == Routes.LOGIN
    val esAdministrador = usuario?.esAdministrador() == true
    val destinos = if (esAdministrador) destinosAdministrador else destinosColaborador
    val rutasSuperiores = destinos.map { it.route.substringBefore("?") }.toSet()
    val rutaBase = route?.substringBefore("?")
    val esSuperior = rutaBase in rutasSuperiores
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { probadorViewModel.mensajes.collect { snackbar.showSnackbar(it) } }
    LaunchedEffect(Unit) { incidenciaViewModel.mensajes.collect { snackbar.showSnackbar(it) } }
    LaunchedEffect(Unit) { usuarioViewModel.mensajes.collect { snackbar.showSnackbar(it) } }

    fun navegarSuperior(destino: String) {
        navController.navigate(destino) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = false }
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = {
            if (!esLogin) {
                TopAppBar(
                    title = { Text(tituloRuta(route)) },
                    navigationIcon = {
                        if (!esSuperior) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    },
                    actions = {
                        if (esSuperior) IconButton(onClick = {
                            loginViewModel.cerrarSesion()
                            navController.navigate(Routes.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                        }) { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (!esLogin && esSuperior) {
                NavigationBar {
                    destinos.forEach { destino ->
                        val base = destino.route.substringBefore("?")
                        NavigationBarItem(
                            selected = rutaBase == base,
                            onClick = { navegarSuperior(destino.route) },
                            icon = { Icon(destino.icono, contentDescription = null) },
                            label = { Text(destino.etiqueta) }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(padding),
            enterTransition = { navEnterTransition },
            exitTransition = { navExitTransition },
            popEnterTransition = { navPopEnterTransition },
            popExitTransition = { navPopExitTransition }
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(login, loginViewModel::cambiarEmail, loginViewModel::cambiarContrasena, loginViewModel::seleccionarSucursal, loginViewModel::usarCuenta) {
                    loginViewModel.iniciarSesion()
                }
                LaunchedEffect(login.usuario?.id) {
                    if (login.usuario != null) navController.navigate(Routes.PANEL) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }
            }
            composable(Routes.PANEL) {
                val disponibles = usuario?.listaSucursales().orEmpty()
                val sucursal = login.sucursalSeleccionada
                PanelProbadoresScreen(
                    probadores, sucursal, disponibles, esAdministrador,
                    if (sucursal == "Todas") colas.values.sum() else colas[sucursal] ?: 0,
                    ahora,
                    loginViewModel::seleccionarSucursal,
                    { probadorViewModel.cambiarCola(sucursal, it) },
                    { navController.navigate(Routes.detalle(it)) }
                )
            }
            composable(Routes.DETALLE, arguments = listOf(navArgument("probadorId") { type = NavType.IntType })) { entryDetalle ->
                val id = entryDetalle.arguments?.getInt("probadorId") ?: 0
                val probador = probadores.firstOrNull { it.id == id }
                DetalleProbadorScreen(
                    probador = probador,
                    usos = usos.filter { it.probadorId == id },
                    incidencias = incidencias.filter { it.probadorId == id },
                    ahora = ahora,
                    cola = probador?.let { colas[it.sucursal] } ?: 0,
                    esAdministrador = esAdministrador,
                    registrarIngreso = { prendas -> probador?.let { probadorViewModel.registrarIngreso(it.id, prendas, it.sucursal) } },
                    registrarSalida = { prendas -> probadorViewModel.registrarSalida(id, prendas, usuario?.nombre.orEmpty()) },
                    clausurar = { probadorViewModel.clausurar(id, it) },
                    habilitar = { probadorViewModel.habilitar(id) },
                    reportar = { navController.navigate(Routes.reporte(id)) },
                    resolverIncidencia = { incidenciaViewModel.resolver(it, usuario?.nombre.orEmpty()) }
                )
            }
            composable(
                Routes.REPORTE,
                arguments = listOf(navArgument("probadorId") { type = NavType.IntType; defaultValue = 0 })
            ) { reporteEntry ->
                ReporteIncidenciaScreen(
                    probadores = probadores,
                    sucursal = login.sucursalSeleccionada,
                    probadorInicial = reporteEntry.arguments?.getInt("probadorId") ?: 0
                ) { probadorId, tipo, descripcion, faltantes, foto ->
                    incidenciaViewModel.guardar(probadorId, tipo, descripcion, faltantes, foto, usuario?.nombre.orEmpty()) {
                        if ((reporteEntry.arguments?.getInt("probadorId") ?: 0) > 0) navController.popBackStack()
                    }
                }
            }
            composable(Routes.ALERTAS) {
                AlertasScreen(probadores, login.sucursalSeleccionada, ahora) { navController.navigate(Routes.detalle(it)) }
            }
            composable(Routes.INCIDENCIAS) {
                IncidenciasScreen(incidencias, probadores, login.sucursalSeleccionada, { incidenciaViewModel.resolver(it, usuario?.nombre.orEmpty()) }) {
                    navController.navigate(Routes.detalle(it))
                }
            }
            composable(Routes.HISTORIAL) { HistorialScreen(usos, probadores, login.sucursalSeleccionada) }
            composable(Routes.CUENTAS) {
                CuentasScreen(usuarios, { navController.navigate(Routes.cuenta()) }, { navController.navigate(Routes.cuenta(it)) }, usuarioViewModel::cambiarEstado)
            }
            composable(Routes.CUENTA, arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })) { cuentaEntry ->
                val id = cuentaEntry.arguments?.getInt("usuarioId") ?: 0
                CuentaFormScreen(usuarios.firstOrNull { it.id == id }, id == 0) { nombre, email, clave, sucursales, activo ->
                    usuarioViewModel.guardar(id, nombre, email, clave, sucursales, activo) { navController.popBackStack() }
                }
            }
        }
    }
}
