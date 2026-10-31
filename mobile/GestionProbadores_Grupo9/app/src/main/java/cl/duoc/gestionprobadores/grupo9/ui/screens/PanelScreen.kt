package cl.duoc.gestionprobadores.grupo9.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.Probador
import cl.duoc.gestionprobadores.grupo9.viewmodel.PanelViewModel

/**
 * Grilla con ~10 probadores. La tarjeta cambia de color según el estado
 * (animateColorAsState) y el ícono de "excedido" parpadea con
 * rememberInfiniteTransition, como pide la rúbrica de animaciones.
 */
@Composable
fun PanelScreen(
    viewModel: PanelViewModel,
    onProbadorClick: (Int) -> Unit
) {
    val probadores by viewModel.probadores.collectAsState()
    val context = LocalContext.current

    val lanzadorPermisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* si lo rechaza, simplemente no se mostrarán notificaciones */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val concedido = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!concedido) {
                lanzadorPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Panel de probadores",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(probadores, key = { it.id }) { probador ->
                TarjetaProbador(probador = probador, onClick = { onProbadorClick(probador.id) })
            }
        }
    }
}

@Composable
private fun TarjetaProbador(probador: Probador, onClick: () -> Unit) {
    val colorObjetivo = when (probador.estado) {
        EstadoProbador.LIBRE -> Color(0xFFE8F5E9)
        EstadoProbador.OCUPADO -> Color(0xFFFFF3E0)
        EstadoProbador.EXCEDIDO -> Color(0xFFFFEBEE)
    }

    val transicion = rememberInfiniteTransition(label = "parpadeo")
    val colorAnimado by transicion.animateColor(
        initialValue = colorObjetivo,
        targetValue = if (probador.estado == EstadoProbador.EXCEDIDO) Color(0xFFFFCDD2) else colorObjetivo,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorTarjeta"
    )

    val alphaIcono by transicion.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500), repeatMode = RepeatMode.Reverse),
        label = "alphaIcono"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorAnimado)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = probador.numero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Column(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                when (probador.estado) {
                    EstadoProbador.LIBRE -> Text("Libre")
                    EstadoProbador.OCUPADO -> Text("Ocupado")
                    EstadoProbador.EXCEDIDO -> androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Tiempo excedido",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.alpha(alphaIcono)
                        )
                        Text(
                            text = " Excedido",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
