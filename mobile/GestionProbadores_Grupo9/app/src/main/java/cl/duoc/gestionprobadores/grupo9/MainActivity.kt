package cl.duoc.gestionprobadores.grupo9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import cl.duoc.gestionprobadores.grupo9.ui.NavegacionPrincipal
import cl.duoc.gestionprobadores.grupo9.ui.theme.GestionProbadores_Grupo9Theme
import cl.duoc.gestionprobadores.grupo9.viewmodel.ColaViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.ProbadorViewModel

class MainActivity : ComponentActivity() {

    private val probadorViewModel: ProbadorViewModel by viewModels()
    private val colaViewModel: ColaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            GestionProbadores_Grupo9Theme {
                NavegacionPrincipal(
                    probadorViewModel = probadorViewModel,
                    colaViewModel = colaViewModel
                )
            }
        }
    }
}