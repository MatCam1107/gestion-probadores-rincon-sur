package cl.duoc.gestionprobadores.grupo9.util

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager

/**
 * Recurso nativo 1: vibración. minSdk de la app es 33, así que se puede usar
 * directo la API moderna (VibratorManager + VibrationEffect) sin fallback a
 * la API vieja de Vibrator.
 */
object VibracionHelper {

    /** Vibra en un patrón corto de "alerta" (dos golpes), como si el teléfono se sacudiera. */
    fun vibrarAlerta(context: Context) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            ?: return
        val vibrator = manager.defaultVibrator
        val patron = longArrayOf(0, 120, 80, 120)
        vibrator.vibrate(VibrationEffect.createWaveform(patron, -1))
    }
}
