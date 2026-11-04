package cl.duoc.gestionprobadores.grupo9.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val esquemaClaro = lightColorScheme(
    primary = AzulMarino,
    onPrimary = Color.White,
    primaryContainer = AzulMarino,
    onPrimaryContainer = Color.White,
    secondary = AzulMarca,
    tertiary = NaranjaMarca,
    background = FondoClaro,
    surface = Color.White,
    error = RojoError
)

private val esquemaAdministrador = darkColorScheme(
    primary = Color(0xFF9EC5FF),
    onPrimary = Color(0xFF07111B),
    primaryContainer = Color(0xFF121826),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF7FB2FF),
    tertiary = NaranjaMarca,
    background = Color(0xFF0B0F17),
    surface = Color(0xFF121826),
    error = Color(0xFFFFB4AB)
)

@Composable
fun GestionProbadoresTheme(
    modoAdministrador: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (modoAdministrador) esquemaAdministrador else esquemaClaro,
        content = content
    )
}
