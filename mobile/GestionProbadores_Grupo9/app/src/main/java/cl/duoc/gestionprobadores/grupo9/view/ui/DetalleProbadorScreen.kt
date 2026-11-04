package cl.duoc.gestionprobadores.grupo9.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.repository.models.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.repository.models.Incidencia
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import cl.duoc.gestionprobadores.grupo9.repository.models.UsoProbador
import cl.duoc.gestionprobadores.grupo9.view.utils.formatearDuracion
import cl.duoc.gestionprobadores.grupo9.view.utils.formatearHora

@Composable
fun DetalleProbadorScreen(
    probador: Probador?,
    usos: List<UsoProbador>,
    incidencias: List<Incidencia>,
    ahora: Long,
    cola: Int,
    esAdministrador: Boolean,
    registrarIngreso: (Int) -> Unit,
    registrarSalida: (Int) -> Unit,
    clausurar: (String) -> Unit,
    habilitar: () -> Unit,
    reportar: () -> Unit,
    resolverIncidencia: (Int) -> Unit
) {
    if (probador == null) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Probador no encontrado")
        }
        return
    }
    var dialogo by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = colorEstado(probador.estado))
        ) {
            Column(Modifier.padding(36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(textoEstado(probador.estado), fontWeight = FontWeight.Bold)
                if (probador.horaInicio != null) {
                    Text(formatearDuracion(ahora - probador.horaInicio), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("de 15:00 permitidos")
                }
            }
        }
        Text(probador.sucursal, style = MaterialTheme.typography.titleMedium)
        if (probador.estado == EstadoProbador.EXCEDIDO && probador.horaInicio != null) {
            Text(
                "+${formatearDuracion(ahora - probador.horaInicio - 15 * 60_000L)} sobre el límite",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DatoDetalle("Desde", probador.horaInicio?.let(::formatearHora) ?: "—", Modifier.weight(1f))
            DatoDetalle("Prendas", if (probador.cantidadPrendas > 0) probador.cantidadPrendas.toString() else "—", Modifier.weight(1f))
            DatoDetalle("En espera", cola.toString(), Modifier.weight(1f))
        }

        if (esAdministrador) {
            Text("Como administrador supervisas; no ingresas ni liberas clientes.", textAlign = TextAlign.Center)
        } else when (probador.estado) {
            EstadoProbador.LIBRE -> {
                Button(onClick = { dialogo = "ingreso" }, modifier = Modifier.fillMaxWidth()) { Text(if (cola > 0) "Entra el siguiente cliente" else "Registrar ingreso") }
                OutlinedButton(onClick = { dialogo = "clausura" }, modifier = Modifier.fillMaxWidth()) { Text("Clausurar probador") }
            }
            EstadoProbador.OCUPADO, EstadoProbador.EXCEDIDO ->
                Button(onClick = { dialogo = "salida" }, modifier = Modifier.fillMaxWidth()) { Text("Marcar como libre") }
            EstadoProbador.CLAUSURADO ->
                Button(onClick = habilitar, modifier = Modifier.fillMaxWidth()) { Text("Habilitar probador") }
            EstadoProbador.REVISAR -> Text("Resuelve la incidencia pendiente para volver a habilitarlo.")
        }
        OutlinedButton(onClick = reportar, modifier = Modifier.fillMaxWidth()) { Text("Reportar incidencia") }

        if (incidencias.isNotEmpty()) {
            Text("Incidencias", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            incidencias.take(3).forEach { incidencia ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(tituloTipoIncidencia(incidencia.tipo), fontWeight = FontWeight.Bold)
                        Text(incidencia.descripcion)
                        Text(if (incidencia.resuelta) "Resuelta" else "Pendiente", color = if (incidencia.resuelta) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        if (!incidencia.resuelta) OutlinedButton(onClick = { resolverIncidencia(incidencia.id) }) { Text("Marcar como resuelta") }
                    }
                }
            }
        }
        Text("Usos recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        usos.take(3).forEach { uso ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${formatearHora(uso.horaIngreso)} a ${formatearHora(uso.horaSalida)}")
                    Text("${uso.duracionMinutos()} min", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (dialogo == "ingreso") {
        DialogoContador("¿Con cuántas prendas ingresa?", 3, 1, 10, "Registrar", { dialogo = null }) {
            dialogo = null
            registrarIngreso(it)
        }
    }
    if (dialogo == "salida") {
        DialogoContador(
            titulo = "¿Con cuántas prendas sale?",
            valorInicial = probador.cantidadPrendas,
            minimo = 0,
            maximo = probador.cantidadPrendas,
            confirmar = "Liberar",
            alCerrar = { dialogo = null },
            alConfirmar = {
                dialogo = null
                registrarSalida(it)
            }
        )
    }
    if (dialogo == "clausura") {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { dialogo = null },
            title = { Text("Clausurar probador") },
            text = { Text("Selecciona el motivo") },
            confirmButton = { Button(onClick = { dialogo = null; clausurar("En limpieza") }) { Text("En limpieza") } },
            dismissButton = { OutlinedButton(onClick = { dialogo = null; clausurar("Sucio") }) { Text("Sucio") } }
        )
    }
}

@Composable
private fun DatoDetalle(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier) { Column(Modifier.padding(12.dp)) { Text(titulo, style = MaterialTheme.typography.labelMedium); Text(valor, fontWeight = FontWeight.Bold) } }
}

fun tituloTipoIncidencia(tipo: String): String = when (tipo) {
    Incidencia.PRENDA_TIENDA -> "Prenda de la tienda"
    Incidencia.OBJETO_CLIENTE -> "Objeto del cliente"
    else -> "Faltan prendas"
}
