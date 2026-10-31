package cl.duoc.gestionprobadores.grupo9.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
/**
 * Anillo que se va llenando según [progreso] (0f a 1f), animado con
 * animateFloatAsState. Se usa en DetalleScreen para mostrar cuánto tiempo
 * de ocupación lleva un probador respecto al umbral configurado.
 */
@Composable
fun AnilloTiempo(
    progreso: Float,
    texto: String,
    modifier: Modifier = Modifier,
    colorAnillo: Color = MaterialTheme.colorScheme.primary
) {
    val progresoAnimado by animateFloatAsState(
        targetValue = progreso.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "progresoAnillo"
    )

    Box(
        modifier = modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val grosor = 14.dp.toPx()
            drawArc(
                color = colorAnillo.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = grosor),
                size = Size(size.width - grosor, size.height - grosor),
                topLeft = androidx.compose.ui.geometry.Offset(grosor / 2, grosor / 2)
            )
            drawArc(
                color = colorAnillo,
                startAngle = -90f,
                sweepAngle = 360f * progresoAnimado,
                useCenter = false,
                style = Stroke(width = grosor),
                size = Size(size.width - grosor, size.height - grosor),
                topLeft = androidx.compose.ui.geometry.Offset(grosor / 2, grosor / 2)
            )
        }
        Text(text = texto, style = MaterialTheme.typography.titleMedium)
    }
}
