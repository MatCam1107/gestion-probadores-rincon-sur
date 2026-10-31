package cl.duoc.gestionprobadores.grupo9.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.gestionprobadores.grupo9.util.CapturaFotoUtil
import cl.duoc.gestionprobadores.grupo9.viewmodel.ReportePrendaUiState

/**
 * Formulario "Prenda olvidada": descripción (validada en el ViewModel) +
 * foto con cámara (o galería como alternativa) vía FileProvider.
 * Vive dentro de DetalleScreen.
 */
@Composable
fun ReportePrendaForm(
    estado: ReportePrendaUiState,
    onDescripcionChange: (String) -> Unit,
    onFotoCapturada: (String?) -> Unit,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var uriPendiente by remember { mutableStateOf<Uri?>(null) }

    val lanzadorCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito -> if (exito) onFotoCapturada(uriPendiente?.toString()) }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) onFotoCapturada(uri.toString()) }

    val lanzadorPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            val uri = CapturaFotoUtil.crearUriParaFoto(context)
            uriPendiente = uri
            lanzadorCamara.launch(uri)
        }
    }

    fun abrirCamara() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            val uri = CapturaFotoUtil.crearUriParaFoto(context)
            uriPendiente = uri
            lanzadorCamara.launch(uri)
        } else {
            lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Reportar prenda olvidada", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción de la prenda") },
                isError = estado.errores.descripcion != null,
                supportingText = { estado.errores.descripcion?.let { Text(it) } },
                trailingIcon = {
                    if (estado.errores.descripcion != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row {
                OutlinedButton(onClick = { abrirCamara() }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Text(" Tomar foto")
                }
                OutlinedButton(
                    onClick = { lanzadorGaleria.launch("image/*") },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Elegir de galería")
                }
            }

            estado.fotoUri?.let { uriTexto ->
                Spacer(Modifier.height(12.dp))
                MiniaturaFoto(uriTexto = uriTexto)
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = onGuardar, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar reporte")
            }
        }
    }
}

/** Decodifica el Uri de la foto a un Bitmap y lo muestra (sin librerías externas). */
@Composable
private fun MiniaturaFoto(uriTexto: String) {
    val context = LocalContext.current
    var bitmap by remember(uriTexto) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uriTexto) {
        bitmap = runCatching {
            val uri = Uri.parse(uriTexto)
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        }.getOrNull()
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Foto de la prenda",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
        )
    }
}
