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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.gestionprobadores.grupo9.repository.models.Incidencia
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import cl.duoc.gestionprobadores.grupo9.view.utils.guardarFotoTemporal
import coil.compose.AsyncImage

@Composable
fun ReporteIncidenciaScreen(
    probadores: List<Probador>,
    sucursal: String,
    probadorInicial: Int,
    guardar: (Int, String, String, Int, String?) -> Unit
) {
    val context = LocalContext.current
    var probadorId by remember(probadorInicial) { mutableIntStateOf(probadorInicial) }
    var tipo by remember { mutableStateOf(Incidencia.PRENDA_TIENDA) }
    var descripcion by remember { mutableStateOf("") }
    var faltantes by remember { mutableIntStateOf(1) }
    var fotoUri by remember { mutableStateOf<String?>(null) }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var tieneEtiqueta by remember { mutableStateOf(true) }
    var sku by remember { mutableStateOf("") }
    var objeto by remember { mutableStateOf("") }
    val visibles = probadores.filter { sucursal == "Todas" || it.sucursal == sucursal }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) fotoUri = guardarFotoTemporal(context, bitmap)
    }
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) camara.launch(null)
    }
    fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) camara.launch(null)
        else permiso.launch(Manifest.permission.CAMERA)
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("¿Qué pasó?", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                Incidencia.PRENDA_TIENDA to "Prenda tienda",
                Incidencia.OBJETO_CLIENTE to "Objeto cliente",
                Incidencia.PRENDAS_FALTANTES to "Faltan prendas"
            ).forEach { (valor, texto) ->
                FilterChip(selected = tipo == valor, onClick = { tipo = valor }, label = { Text(texto) })
            }
        }
        Text("Probador", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            visibles.take(6).forEach { probador ->
                FilterChip(selected = probadorId == probador.id, onClick = { probadorId = probador.id }, label = { Text(probador.numero.toString()) })
            }
        }
        if (tipo == Incidencia.PRENDAS_FALTANTES) {
            Text("¿Cuántas prendas faltan?")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { faltantes = (faltantes - 1).coerceAtLeast(1) }) { Text("−") }
                Text(faltantes.toString(), style = MaterialTheme.typography.headlineSmall)
                OutlinedButton(onClick = { faltantes = (faltantes + 1).coerceAtMost(10) }) { Text("+") }
            }
        } else if (tipo == Incidencia.PRENDA_TIENDA) {
            Text("Datos de la prenda", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(marca, { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(modelo, { modelo = it }, label = { Text("Modelo (opcional)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(talla, { talla = it.take(4) }, label = { Text("Talla") }, modifier = Modifier.weight(1f))
            }
            Text("¿Tiene etiqueta?")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = tieneEtiqueta, onClick = { tieneEtiqueta = true }, label = { Text("Con etiqueta") })
                FilterChip(selected = !tieneEtiqueta, onClick = { tieneEtiqueta = false; sku = "" }, label = { Text("Sin etiqueta") })
            }
            if (tieneEtiqueta) {
                OutlinedTextField(
                    sku,
                    { sku = it.filter(Char::isDigit).take(4) },
                    label = { Text("Últimos 4 dígitos del SKU") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Text("¿Qué se quedó?", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Billetera", "Cartera o bolso", "Celular", "Llaves", "Prenda del cliente", "Otro").forEach { opcion ->
                    FilterChip(selected = objeto == opcion, onClick = { objeto = opcion }, label = { Text(opcion) })
                }
            }
        }
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text(if (tipo == Incidencia.PRENDAS_FALTANTES) "¿Qué prendas faltan?" else "Descripción") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        if (tipo != Incidencia.PRENDAS_FALTANTES) {
            fotoUri?.let { AsyncImage(model = it, contentDescription = "Foto de la incidencia", modifier = Modifier.fillMaxWidth().height(180.dp)) }
            OutlinedButton(onClick = ::abrirCamara, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Text(if (fotoUri == null) "Tomar foto" else "Cambiar foto", modifier = Modifier.padding(start = 8.dp))
            }
        }
        val formularioValido = when (tipo) {
            Incidencia.PRENDAS_FALTANTES -> descripcion.trim().length >= 5
            Incidencia.OBJETO_CLIENTE -> objeto.isNotBlank() && descripcion.trim().length >= 5 && fotoUri != null
            else -> marca.isNotBlank() && talla.isNotBlank() && descripcion.trim().length >= 5 &&
                fotoUri != null && (!tieneEtiqueta || sku.length == 4)
        }
        Button(
            onClick = {
                val detalle = when (tipo) {
                    Incidencia.PRENDA_TIENDA -> buildString {
                        append("Marca: ${marca.trim()}")
                        if (modelo.isNotBlank()) append(", modelo: ${modelo.trim()}")
                        append(", talla: ${talla.trim().uppercase()}")
                        append(if (tieneEtiqueta) ", SKU terminado en $sku. " else ", sin etiqueta. ")
                        append(descripcion.trim())
                    }
                    Incidencia.OBJETO_CLIENTE -> "$objeto. ${descripcion.trim()}"
                    else -> descripcion.trim()
                }
                guardar(probadorId, tipo, detalle, faltantes, fotoUri)
            },
            enabled = probadorId > 0 && formularioValido,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar incidencia") }
    }
}
