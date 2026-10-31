package cl.duoc.gestionprobadores.grupo9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.gestionprobadores.grupo9.ui.navigation.GestionProbadoresApp
import cl.duoc.gestionprobadores.grupo9.ui.theme.GestionProbadores_Grupo9Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            GestionProbadores_Grupo9Theme {
                GestionProbadoresApp(application = application as GestionProbadoresApplication)
            }
        }
    }
}
