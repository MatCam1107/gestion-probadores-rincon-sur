package cl.duoc.gestionprobadores.grupo9.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.repository.models.Incidencia
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import cl.duoc.gestionprobadores.grupo9.view.utils.formatearFecha

@Composable
fun IncidenciasScreen(
    incidencias: List<Incidencia>,
    probadores: List<Probador>,
    sucursal: String,
    resolver: (Int) -> Unit,
    abrirProbador: (Int) -> Unit
) {
    var filtro by remember { mutableStateOf("PENDIENTES") }
    val ids = probadores.filter { sucursal == "Todas" || it.sucursal == sucursal }.map { it.id }.toSet()
    val visibles = incidencias.filter { it.probadorId in ids }.filter {
        filtro == "TODAS" || (filtro == "PENDIENTES" && !it.resuelta) || (filtro == "RESUELTAS" && it.resuelta)
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("PENDIENTES" to "Pendientes", "RESUELTAS" to "Resueltas", "TODAS" to "Todas").forEach { (valor, texto) ->
                    FilterChip(selected = filtro == valor, onClick = { filtro = valor }, label = { Text(texto) })
                }
            }
        }
        item { Text("Mientras una incidencia esté pendiente, el probador permanece en Revisar.", style = MaterialTheme.typography.bodyMedium) }
        if (visibles.isEmpty()) item { Text("No hay incidencias en esta lista.") }
        items(visibles, key = { it.id }) { incidencia ->
            val probador = probadores.firstOrNull { it.id == incidencia.probadorId }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(tituloTipoIncidencia(incidencia.tipo), fontWeight = FontWeight.Bold)
                    Text("${probador?.sucursal.orEmpty()} · Probador ${probador?.numero ?: "—"}")
                    Text(formatearFecha(incidencia.fecha), style = MaterialTheme.typography.labelMedium)
                    Text(incidencia.descripcion)
                    Text(if (incidencia.resuelta) "Resuelta" else "Pendiente", color = if (incidencia.resuelta) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { abrirProbador(incidencia.probadorId) }) { Text("Ver probador") }
                        if (!incidencia.resuelta) OutlinedButton(onClick = { resolver(incidencia.id) }) { Text("Marcar resuelta") }
                    }
                }
            }
        }
    }
}
