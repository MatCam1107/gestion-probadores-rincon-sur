package cl.duoc.gestionprobadores.grupo9.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.viewmodel.FilaHistorial
import cl.duoc.gestionprobadores.grupo9.viewmodel.HistorialViewModel
import kotlin.math.roundToInt

/**
 * Historial: promedio general (calculado con AVG() en Room) + un gráfico de
 * barras simple por probador (agrupado en Kotlin a partir de esos mismos
 * datos), con cada barra creciendo con animateDpAsState.
 */
@Composable
fun HistorialScreen(viewModel: HistorialViewModel) {
    val estado by viewModel.uiState.collectAsState()

    val promediosPorProbador = remember(estado.filas) {
        estado.filas
            .filter { it.duracionMinutos != null }
            .groupBy { it.numeroProbador }
            .mapValues { (_, filas) -> filas.mapNotNull { it.duracionMinutos }.average() }
            .toList()
            .sortedBy { it.first }
    }

    val maximo = (promediosPorProbador.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial de uso", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Promedio general de ocupación", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = estado.promedioMinutos?.let { "${it.roundToInt()} min" } ?: "Sin datos aún",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Promedio por probador", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (promediosPorProbador.isEmpty()) {
            Text("Todavía no hay usos registrados.", style = MaterialTheme.typography.bodyMedium)
        }

        LazyColumn {
            items(promediosPorProbador) { (numero, promedio) ->
                FilaBarra(numero = numero, promedio = promedio, maximo = maximo)
            }
        }
    }
}

@Composable
private fun FilaBarra(numero: String, promedio: Double, maximo: Double) {
    val anchoObjetivo = (180.dp * (promedio / maximo).toFloat()).coerceAtLeast(4.dp)
    val anchoAnimado by animateDpAsState(
        targetValue = anchoObjetivo,
        animationSpec = tween(durationMillis = 600),
        label = "barra_$numero"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Text(
            text = numero,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(90.dp)
        )
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .height(16.dp)
                .width(anchoAnimado)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
        )
        Text(
            text = " ${promedio.roundToInt()} min",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
