package cl.duoc.gestionprobadores.grupo9.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.gestionprobadores.grupo9.GestionProbadoresApplication
import cl.duoc.gestionprobadores.grupo9.data.ConfigPreferences
import cl.duoc.gestionprobadores.grupo9.data.ConfigValores
import cl.duoc.gestionprobadores.grupo9.util.SesionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado SOLO del campo de texto "umbral de minutos" (el que sí necesita validación). */
data class UmbralUiState(
    val texto: String = "",
    val error: String? = null,
    val guardadoOk: Boolean = false,
    val inicializado: Boolean = false
)

/**
 * Pantalla de Configuración. Los switches (notificaciones, vibración) se
 * leen y escriben directo contra DataStore a través de [config], porque un
 * booleano no necesita "validación". El umbral de minutos sí, porque es
 * texto libre: vive en [umbralUiState] con su propio error por campo, y
 * solo se guarda en DataStore cuando pasa [validarUmbral].
 */
class ConfigViewModel(
    private val configPreferences: ConfigPreferences,
    private val sesionManager: SesionManager
) : ViewModel() {

    val config: StateFlow<ConfigValores> = configPreferences.configFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConfigValores())

    private val _umbralUiState = MutableStateFlow(UmbralUiState())
    val umbralUiState: StateFlow<UmbralUiState> = _umbralUiState.asStateFlow()

    init {
        // La primera vez que llega el valor guardado, precarga el campo de texto.
        viewModelScope.launch {
            val valores = configPreferences.configFlow
            valores.collect { valor ->
                if (!_umbralUiState.value.inicializado) {
                    _umbralUiState.update {
                        it.copy(texto = valor.umbralMinutos.toString(), inicializado = true)
                    }
                }
            }
        }
    }

    fun onUmbralTextoChange(valor: String) {
        if (valor.isEmpty() || valor.all { it.isDigit() }) {
            _umbralUiState.update { it.copy(texto = valor, error = null, guardadoOk = false) }
        }
    }

    private fun validarUmbral(): Boolean {
        val texto = _umbralUiState.value.texto
        val minutos = texto.toIntOrNull()
        val error = when {
            texto.isBlank() -> "Ingresa un valor"
            minutos == null -> "Debe ser un número"
            minutos <= 0 -> "Debe ser mayor a 0"
            minutos > 180 -> "Máximo 180 minutos"
            else -> null
        }
        _umbralUiState.update { it.copy(error = error) }
        return error == null
    }

    fun guardarUmbral() {
        if (!validarUmbral()) return
        viewModelScope.launch {
            configPreferences.actualizarUmbral(_umbralUiState.value.texto.toInt())
            _umbralUiState.update { it.copy(guardadoOk = true) }
        }
    }

    fun actualizarNotificaciones(activas: Boolean) {
        viewModelScope.launch { configPreferences.actualizarNotificaciones(activas) }
    }

    fun actualizarVibracion(activa: Boolean) {
        viewModelScope.launch { configPreferences.actualizarVibracion(activa) }
    }

    fun cerrarSesion() {
        sesionManager.cerrarSesion()
    }

    companion object {
        fun factory(app: GestionProbadoresApplication) = viewModelFactory {
            initializer {
                ConfigViewModel(app.container.configPreferences, app.container.sesionManager)
            }
        }
    }
}
