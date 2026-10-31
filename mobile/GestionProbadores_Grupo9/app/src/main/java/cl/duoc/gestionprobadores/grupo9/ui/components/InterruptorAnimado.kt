package cl.duoc.gestionprobadores.grupo9.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue

/**
 * Switch hecho a mano con animateFloatAsState moviendo el círculo, para que
 * se vea explícitamente la animación pedida ("el switch se desliza") en vez
 * de depender de la animación interna del Switch de Material3.
 */
@Composable
fun InterruptorAnimado(
    activo: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val desplazamiento by animateFloatAsState(
        targetValue = if (activo) 20f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "desplazamientoSwitch"
    )

    val colorFondo = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .width(44.dp)
            .height(24.dp)
            .background(colorFondo, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCambio(!activo) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = desplazamiento.dp + 2.dp)
                .size(20.dp)
                .background(Color.White, CircleShape)
        )
    }
}
