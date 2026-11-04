package cl.duoc.gestionprobadores.grupo9.view.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.duoc.gestionprobadores.grupo9.R

private const val CANAL_ALERTAS = "alertas_probadores"

fun probarAlerta(context: Context, vibrar: Boolean): Boolean {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        return false
    }
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(
        NotificationChannel(CANAL_ALERTAS, "Alertas de probadores", NotificationManager.IMPORTANCE_HIGH)
    )
    val notificacion = NotificationCompat.Builder(context, CANAL_ALERTAS)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Probador excedido")
        .setContentText("Un probador superó el límite de 15 minutos.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(context).notify(1500, notificacion)
    if (vibrar) {
        context.getSystemService(VibratorManager::class.java).defaultVibrator
            .vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300), -1))
    }
    return true
}
