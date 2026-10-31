package cl.duoc.gestionprobadores.grupo9.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import cl.duoc.gestionprobadores.grupo9.R

/**
 * Recurso nativo 2: notificaciones. Crea un canal (obligatorio desde
 * Android 8) y muestra una notificación por cada alerta generada. Antes de
 * notificar comprueba el permiso POST_NOTIFICATIONS (obligatorio en tiempo
 * de ejecución desde Android 13, que es el minSdk de este proyecto); la
 * pantalla que llama a esto es responsable de haber pedido el permiso.
 */
object NotificacionHelper {

    private const val CANAL_ID = "alertas_probadores"

    fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL_ID,
            "Alertas de probadores",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos de tiempo excedido y prendas olvidadas"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }

    fun mostrarAlerta(context: Context, id: Int, titulo: String, mensaje: String) {
        val tienePermiso = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!tienePermiso) return

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notificacion)
    }
}
