package cl.duoc.gestionprobadores.grupo9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import cl.duoc.gestionprobadores.grupo9.ui.PanelProbadoresScreen
import cl.duoc.gestionprobadores.grupo9.ui.theme.GestionProbadores_Grupo9Theme
import cl.duoc.gestionprobadores.grupo9.viewmodel.ProbadorViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ProbadorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            GestionProbadores_Grupo9Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        PanelProbadoresScreen(
                            probadores = viewModel.probadores
                        )
                    }
                }
            }
        }
    }
}