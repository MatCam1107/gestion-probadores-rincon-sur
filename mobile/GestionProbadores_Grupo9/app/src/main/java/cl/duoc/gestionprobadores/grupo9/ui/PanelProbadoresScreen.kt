package cl.duoc.gestionprobadores.grupo9.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.Probador

@Composable
fun PanelProbadoresScreen(
    probadores: List<Probador>,
    onProbadorClick: (Probador) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Panel de probadores",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(probadores) { probador ->
                TarjetaProbador(
                    probador = probador,
                    onClick = { onProbadorClick(probador) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaProbador(
    probador: Probador,
    onClick: () -> Unit
) {
    val textoEstado = when (probador.estado) {
        EstadoProbador.DISPONIBLE -> "Disponible"
        EstadoProbador.OCUPADO -> "Ocupado"
        EstadoProbador.CON_INCIDENCIA -> "Con incidencia"
    }

    val colorEstado = when (probador.estado) {
        EstadoProbador.DISPONIBLE -> Color(0xFFE8F5E9)
        EstadoProbador.OCUPADO -> Color(0xFFFFF3E0)
        EstadoProbador.CON_INCIDENCIA -> Color(0xFFFFEBEE)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorEstado
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = probador.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = textoEstado,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (probador.cantidadPrendas > 0) {
                Text(
                    text = "Prendas: ${probador.cantidadPrendas}",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}