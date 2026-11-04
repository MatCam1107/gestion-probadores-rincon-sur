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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.repository.models.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import cl.duoc.gestionprobadores.grupo9.view.utils.formatearDuracion

@Composable
fun PanelProbadoresScreen(
    probadores: List<Probador>,
    sucursal: String,
    sucursalesDisponibles: List<String>,
    esAdministrador: Boolean,
    cola: Int,
    ahora: Long,
    seleccionarSucursal: (String) -> Unit,
    cambiarCola: (Int) -> Unit,
    abrirProbador: (Int) -> Unit
) {
    var filtro by remember { mutableStateOf<EstadoProbador?>(null) }
    val visiblesSucursal = probadores.filter { sucursal == "Todas" || it.sucursal == sucursal }
    val visibles = visiblesSucursal.filter { filtro == null || it.estado == filtro }
    val conteos = EstadoProbador.entries.associateWith { estado -> visiblesSucursal.count { it.estado == estado } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SelectorSucursal(
                seleccionada = sucursal,
                opciones = if (esAdministrador) listOf("Todas") + SUCURSALES else sucursalesDisponibles,
                alSeleccionar = seleccionarSucursal,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        item {
            Text("${visiblesSucursal.size} probadores · límite de 15 min", style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TarjetaResumen("Libres", conteos[EstadoProbador.LIBRE] ?: 0, colorEstado(EstadoProbador.LIBRE), Modifier.weight(1f))
                TarjetaResumen("Ocupados", conteos[EstadoProbador.OCUPADO] ?: 0, colorEstado(EstadoProbador.OCUPADO), Modifier.weight(1f))
                TarjetaResumen("Excedidos", conteos[EstadoProbador.EXCEDIDO] ?: 0, colorEstado(EstadoProbador.EXCEDIDO), Modifier.weight(1f))
            }
        }
        if (sucursal != "Todas") {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Clientes en espera", fontWeight = FontWeight.Bold)
                            Text(if (cola == 0) "Sin espera por ahora" else "Espera estimada: ${cola * 10 / visiblesSucursal.size.coerceAtLeast(1)} min")
                        }
                        if (!esAdministrador) IconButton(onClick = { cambiarCola(-1) }) { Icon(Icons.Default.Remove, "Restar") }
                        Text(cola.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        if (!esAdministrador) IconButton(onClick = { cambiarCola(1) }) { Icon(Icons.Default.Add, "Sumar") }
                    }
                }
            }
            val siguienteLibre = visiblesSucursal.firstOrNull { it.estado == EstadoProbador.LIBRE }
            if (!esAdministrador && cola > 0 && siguienteLibre != null) {
                item {
                    Button(onClick = { abrirProbador(siguienteLibre.id) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Asignar Probador ${siguienteLibre.numero} al siguiente cliente")
                    }
                }
            }
        }
        if (esAdministrador && sucursal == "Todas") {
            item { Text("Sucursales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            items(SUCURSALES) { nombreSucursal ->
                val salas = probadores.filter { it.sucursal == nombreSucursal }
                val libres = salas.count { it.estado == EstadoProbador.LIBRE }
                val ocupados = salas.count { it.estado == EstadoProbador.OCUPADO }
                val problemas = salas.count { it.estado == EstadoProbador.EXCEDIDO || it.estado == EstadoProbador.REVISAR }
                Card(onClick = { seleccionarSucursal(nombreSucursal) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(nombreSucursal, fontWeight = FontWeight.Bold)
                        Text("${salas.size} probadores · $libres libres · $ocupados ocupados · $problemas por revisar")
                    }
                }
            }
        } else {
            item {
                Text("Probadores", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = filtro == null, onClick = { filtro = null }, label = { Text("Todos") })
                    listOf(EstadoProbador.LIBRE, EstadoProbador.OCUPADO, EstadoProbador.EXCEDIDO, EstadoProbador.REVISAR, EstadoProbador.CLAUSURADO).forEach { estado ->
                        FilterChip(selected = filtro == estado, onClick = { filtro = estado }, label = { Text(textoEstado(estado)) })
                    }
                }
            }
            items(visibles, key = { it.id }) { probador ->
                Card(
                    onClick = { abrirProbador(probador.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorEstado(probador.estado),
                        contentColor = androidx.compose.ui.graphics.Color(0xFF1F1F1F)
                    )
                ) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Probador ${probador.numero}", fontWeight = FontWeight.Bold)
                            Text(textoEstado(probador.estado))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            probador.horaInicio?.let { Text(formatearDuracion(ahora - it), fontWeight = FontWeight.Bold) }
                            if (probador.cantidadPrendas > 0) Text("${probador.cantidadPrendas} prendas")
                            probador.motivoClausura?.let { Text(it) }
                        }
                    }
                }
            }
        }
    }
}
