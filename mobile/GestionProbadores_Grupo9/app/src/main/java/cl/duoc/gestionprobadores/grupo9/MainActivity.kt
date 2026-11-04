package cl.duoc.gestionprobadores.grupo9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.gestionprobadores.grupo9.navegations.AppNavigation
import cl.duoc.gestionprobadores.grupo9.view.theme.GestionProbadoresTheme
import cl.duoc.gestionprobadores.grupo9.viewmodels.IncidenciaViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodels.LoginViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodels.ProbadorViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodels.UsuarioViewModel

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val probadorViewModel: ProbadorViewModel by viewModels()
    private val incidenciaViewModel: IncidenciaViewModel by viewModels()
    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
            GestionProbadoresTheme(modoAdministrador = loginState.usuario?.esAdministrador() == true) {
                AppNavigation(loginViewModel, probadorViewModel, incidenciaViewModel, usuarioViewModel)
            }
        }
    }
}
