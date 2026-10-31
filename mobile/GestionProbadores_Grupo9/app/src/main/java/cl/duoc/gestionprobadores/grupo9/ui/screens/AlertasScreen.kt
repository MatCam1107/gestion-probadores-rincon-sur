package cl.duoc.gestionprobadores.grupo9.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.model.Alerta
import cl.duoc.gestionprobadores.grupo9.model.TipoAlerta
import cl.duoc.gestionprobadores.grupo9.viewmodel.AlertasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Lista de alertas (tiempo excedido / prenda olvidada). Cada tarjeta entra
 * deslizándose desde arriba (como una notificación) y las pendientes tienen
 * un pequeño ícono que "tiembla" representando la vibración del celular.
 */
@Composable
fun AlertasScreen(viewModel: AlertasViewModel) {
    val alertas by viewModel.alertas.collectAsState()
    val pendientes by viewModel.cantidadPendientes.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Alertas", style = MaterialTheme.typography.headlineSmall)
            if (pendientes > 0) {
                Text(
                    text = "  ($pendientes sin atender)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (alertas.isEmpty()) {
            Text(
                "No hay alertas todavía.",
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 12.dp)) {
            items(alertas, key = { it.id }) { alerta ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                ) {
                    TarjetaAlerta(alerta = alerta, onAtender = { viewModel.marcarAtendida(alerta) })
                }
            }
        }
    }
}

@Composable
private fun TarjetaAlerta(alerta: Alerta, onAtender: () -> Unit) {
    val formatoHora = remember(alerta.timestamp) {
        SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(alerta.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alerta.atendida) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoAlerta(tipo = alerta.tipo, atendida = alerta.atendida)
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = if (alerta.tipo == TipoAlerta.TIEMPO_EXCEDIDO) "Tiempo excedido" else "Prenda olvidada",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Probador ${alerta.probadorId} · $formatoHora",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (!alerta.atendida) {
                IconButton(onClick = onAtender) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Marcar atendida")
                }
            }
        }
    }
}

@Composable
private fun IconoAlerta(tipo: TipoAlerta, atendida: Boolean) {
    val transicion = rememberInfiniteTransition(label = "sacudida")
    val desplazamiento by transicion.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(150), repeatMode = RepeatMode.Reverse),
        label = "shakeX"
    )

    Icon(
        imageVector = if (tipo == TipoAlerta.TIEMPO_EXCEDIDO) Icons.Default.Timer else Icons.Default.Checkroom,
        contentDescription = null,
        tint = if (atendida) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
        modifier = if (atendida) Modifier else Modifier.graphicsLayer { translationX = desplazamiento }
    )
}

