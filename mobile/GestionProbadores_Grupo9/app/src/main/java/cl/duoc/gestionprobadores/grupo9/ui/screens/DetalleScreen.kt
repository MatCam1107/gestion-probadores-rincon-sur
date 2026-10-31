package cl.duoc.gestionprobadores.grupo9.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.ui.components.AnilloTiempo
import cl.duoc.gestionprobadores.grupo9.ui.components.ReportePrendaForm
import cl.duoc.gestionprobadores.grupo9.viewmodel.PanelViewModel
import cl.duoc.gestionprobadores.grupo9.viewmodel.ReportePrendaViewModel
import kotlinx.coroutines.delay

/**
 * Ficha de un probador: estado, anillo de tiempo, acciones (ocupar/liberar)
 * y el formulario de prenda olvidada (colapsable).
 */
@Composable
fun DetalleScreen(
    probadorId: Int,
    panelViewModel: PanelViewModel,
    reporteViewModel: ReportePrendaViewModel,
    onVolver: () -> Unit
) {
    LaunchedEffect(probadorId) {
        panelViewModel.seleccionarProbador(probadorId)
        reporteViewModel.inicializar(probadorId)
    }

    val probador by panelViewModel.probadorSeleccionado.collectAsState()
    val usos by panelViewModel.usosRecientes.collectAsState()
    val config by panelViewModel.config.collectAsState()
    val reporteEstado by reporteViewModel.uiState.collectAsState()

    var mostrarFormulario by remember { mutableStateOf(false) }
    var ahora by remember { mutableStateOf(System.currentTimeMillis()) }

    // Tick cada segundo SOLO para animar el anillo mientras la pantalla está abierta.
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            ahora = System.currentTimeMillis()
        }
    }

    LaunchedEffect(reporteEstado.guardadoExitoso) {
        if (reporteEstado.guardadoExitoso) {
            mostrarFormulario = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = probador?.numero ?: "Probador",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        val probadorActual = probador
        if (probadorActual == null) {
            Text("Cargando...", modifier = Modifier.padding(16.dp))
            return@Column
        }

        Spacer(Modifier.height(16.dp))

        val progreso = if (probadorActual.estado != EstadoProbador.LIBRE && probadorActual.horaInicioOcupacion != null) {
            val umbralMs = (config.umbralMinutos * 60_000L).coerceAtLeast(1L)
            ((ahora - probadorActual.horaInicioOcupacion) / umbralMs.toFloat())
        } else 0f

        val textoAnillo = when (probadorActual.estado) {
            EstadoProbador.LIBRE -> "Libre"
            else -> {
                val minutos = ((ahora - (probadorActual.horaInicioOcupacion ?: ahora)) / 60000L)
                "$minutos min"
            }
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            AnilloTiempo(
                progreso = progreso,
                texto = textoAnillo,
                colorAnillo = if (probadorActual.estado == EstadoProbador.EXCEDIDO) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        when (probadorActual.estado) {
            EstadoProbador.LIBRE -> Button(
                onClick = { panelViewModel.ocupar(probadorActual.id) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Marcar ocupado") }

            else -> Button(
                onClick = { panelViewModel.marcarLibre(probadorActual.id) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Marcar libre") }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { mostrarFormulario = !mostrarFormulario },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (mostrarFormulario) "Ocultar formulario" else "Reportar prenda olvidada")
        }

        AnimatedVisibility(visible = mostrarFormulario, enter = expandVertically(), exit = shrinkVertically()) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                ReportePrendaForm(
                    estado = reporteEstado,
                    onDescripcionChange = reporteViewModel::onDescripcionChange,
                    onFotoCapturada = reporteViewModel::onFotoCapturada,
                    onGuardar = reporteViewModel::guardar
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Usos recientes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (usos.isEmpty()) {
            Text("Todavía no hay registros para este probador.", style = MaterialTheme.typography.bodySmall)
        } else {
            usos.take(5).forEach { uso ->
                val duracionTexto = uso.duracionMinutos?.let { "$it min" } ?: "en curso"
                Text("• ${duracionTexto}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
