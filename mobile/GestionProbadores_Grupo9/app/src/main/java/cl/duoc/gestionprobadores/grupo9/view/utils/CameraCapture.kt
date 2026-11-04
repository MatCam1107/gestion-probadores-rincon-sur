package cl.duoc.gestionprobadores.grupo9.view.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun guardarFotoTemporal(context: Context, bitmap: Bitmap): String {
    val archivo = File(context.cacheDir, "incidencia_${System.currentTimeMillis()}.jpg")
    FileOutputStream(archivo).use { salida ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, salida)
    }
    return archivo.toURI().toString()
}
