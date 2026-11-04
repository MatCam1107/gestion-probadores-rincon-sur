package cl.duoc.gestionprobadores.grupo9.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import cl.duoc.gestionprobadores.grupo9.repository.models.UsoProbador
import cl.duoc.gestionprobadores.grupo9.view.utils.formatearFecha

@Composable
fun HistorialScreen(usos: List<UsoProbador>, probadores: List<Probador>, sucursal: String) {
    var filtro by remember { mutableStateOf("TODOS") }
    val ids = probadores.filter { sucursal == "Todas" || it.sucursal == sucursal }.map { it.id }.toSet()
    val visibles = usos.filter { it.probadorId in ids }.filter {
        filtro == "TODOS" || (filtro == "NORMAL" && !it.excedioTiempo() && !it.tieneFaltante()) ||
            (filtro == "EXCEDIDO" && it.excedioTiempo()) || (filtro == "FALTANTE" && it.tieneFaltante())
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("TODOS" to "Todos", "NORMAL" to "Normal", "EXCEDIDO" to "Excedido", "FALTANTE" to "Con faltante").forEach { (valor, texto) ->
                    FilterChip(selected = filtro == valor, onClick = { filtro = valor }, label = { Text(texto) })
                }
            }
        }
        if (visibles.isEmpty()) item { Text("Todavía no hay usos con este filtro.") }
        items(visibles, key = { it.id }) { uso ->
            val probador = probadores.firstOrNull { it.id == uso.probadorId }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text("${probador?.sucursal.orEmpty()} · Probador ${probador?.numero ?: "—"}", fontWeight = FontWeight.Bold)
                    Text(formatearFecha(uso.horaSalida))
                    Text("${uso.duracionMinutos()} min · salieron ${uso.prendasSalida} de ${uso.prendasEntrada} prendas")
                    Text(when { uso.tieneFaltante() -> "Con faltante"; uso.excedioTiempo() -> "Excedido"; else -> "Normal" }, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
