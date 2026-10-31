package cl.duoc.gestionprobadores.grupo9.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Recurso nativo 3: cámara. Crea un archivo nuevo en el almacenamiento
 * privado de la app y devuelve su Uri "segura" (content://) a través de
 * FileProvider, que es lo que exige CameraX/la cámara del sistema desde
 * Android 7 para poder escribir la foto ahí.
 */
object CapturaFotoUtil {

    fun crearUriParaFoto(context: Context): Uri {
        val carpeta = File(context.filesDir, "prendas_olvidadas").apply { mkdirs() }
        val nombre = "prenda_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date()) + ".jpg"
        val archivo = File(carpeta, nombre)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }
}
