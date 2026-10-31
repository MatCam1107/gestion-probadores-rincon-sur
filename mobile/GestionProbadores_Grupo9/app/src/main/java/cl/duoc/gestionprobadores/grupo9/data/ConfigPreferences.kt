package cl.duoc.gestionprobadores.grupo9.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Extensión a nivel de archivo: crea el DataStore "config" una sola vez por Context. */
private val Context.configDataStore by preferencesDataStore(name = "config_probadores")

/**
 * Valores de configuración que persisten entre sesiones con DataStore
 * Preferences (no hace falta Room para pares clave/valor simples como estos).
 */
data class ConfigValores(
    val umbralMinutos: Int = 10,
    val notificacionesActivas: Boolean = true,
    val vibracionActiva: Boolean = true
)

/** Acceso de solo lectura/escritura a las preferencias de configuración. */
class ConfigPreferences(private val context: Context) {

    private object Claves {
        val UMBRAL_MINUTOS = intPreferencesKey("umbral_minutos")
        val NOTIFICACIONES_ACTIVAS = booleanPreferencesKey("notificaciones_activas")
        val VIBRACION_ACTIVA = booleanPreferencesKey("vibracion_activa")
    }

    val configFlow: Flow<ConfigValores> = context.configDataStore.data.map { prefs ->
        ConfigValores(
            umbralMinutos = prefs[Claves.UMBRAL_MINUTOS] ?: 10,
            notificacionesActivas = prefs[Claves.NOTIFICACIONES_ACTIVAS] ?: true,
            vibracionActiva = prefs[Claves.VIBRACION_ACTIVA] ?: true
        )
    }

    suspend fun actualizarUmbral(minutos: Int) {
        context.configDataStore.edit { it[Claves.UMBRAL_MINUTOS] = minutos }
    }

    suspend fun actualizarNotificaciones(activas: Boolean) {
        context.configDataStore.edit { it[Claves.NOTIFICACIONES_ACTIVAS] = activas }
    }

    suspend fun actualizarVibracion(activa: Boolean) {
        context.configDataStore.edit { it[Claves.VIBRACION_ACTIVA] = activa }
    }
}
