package cl.duoc.gestionprobadores.grupo9.view.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import cl.duoc.gestionprobadores.grupo9.view.utils.formatearDuracion
import cl.duoc.gestionprobadores.grupo9.view.utils.probarAlerta
import androidx.core.content.ContextCompat

@Composable
fun AlertasScreen(
    probadores: List<Probador>,
    sucursal: String,
    ahora: Long,
    abrirProbador: (Int) -> Unit
) {
    val context = LocalContext.current
    var vibracion by remember { mutableStateOf(true) }
    var permisoConcedido by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }
    val solicitarPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permisoConcedido = it
        if (it) probarAlerta(context, vibracion)
    }
    val alertas = probadores.filter { probador ->
        (sucursal == "Todas" || probador.sucursal == sucursal) &&
            probador.horaInicio != null && ahora - probador.horaInicio >= 13 * 60_000L
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("Vibración", fontWeight = FontWeight.Bold)
                        Text("Refuerza la alerta en el celular", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = vibracion, onCheckedChange = { vibracion = it })
                }
            }
        }
        item { Text("Alertas activas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        if (alertas.isEmpty()) item { Text("No hay alertas en esta sucursal.") }
        items(alertas, key = { it.id }) { probador ->
            val transcurrido = ahora - (probador.horaInicio ?: ahora)
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Column(Modifier.weight(1f).padding(start = 10.dp)) {
                        Text("Probador ${probador.numero} ${if (transcurrido >= 15 * 60_000L) "excedido" else "cerca del límite"}", fontWeight = FontWeight.Bold)
                        Text("${probador.sucursal} · ${formatearDuracion(transcurrido)}")
                        OutlinedButton(onClick = { abrirProbador(probador.id) }) { Text("Ver probador") }
                    }
                }
            }
        }
        item {
            OutlinedButton(
                onClick = {
                    if (permisoConcedido) probarAlerta(context, vibracion)
                    else solicitarPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Probar notificación y vibración") }
        }
    }
}
