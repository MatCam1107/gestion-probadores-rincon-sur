package cl.duoc.gestionprobadores.grupo9.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.gestionprobadores.grupo9.GestionProbadoresApplication
import cl.duoc.gestionprobadores.grupo9.model.RegistroCola
import cl.duoc.gestionprobadores.grupo9.repository.ColaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ColaErrores(val cantidad: String? = null)

data class ColaUiState(
    val cantidadTexto: String = "",
    val errores: ColaErrores = ColaErrores(),
    val isLoading: Boolean = false,
    val mostrarConfirmacion: Boolean = false
)

class ColaViewModel(private val colaRepository: ColaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ColaUiState())
    val uiState: StateFlow<ColaUiState> = _uiState.asStateFlow()

    val historial: StateFlow<List<RegistroCola>> = colaRepository.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val ultimoRegistro: StateFlow<RegistroCola?> = colaRepository.observarUltimo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onCantidadChange(valor: String) {
        // Solo dígitos, como en un formulario numérico validado.
        if (valor.isEmpty() || valor.all { it.isDigit() }) {
            _uiState.update { it.copy(cantidadTexto = valor, errores = ColaErrores()) }
        }
    }

    private fun validar(): Boolean {
        val texto = _uiState.value.cantidadTexto
        val error = when {
            texto.isBlank() -> "Ingresa un número"
            texto.toIntOrNull() == null -> "Debe ser un número"
            texto.toInt() < 0 -> "No puede ser negativo"
            texto.toInt() > 99 -> "Máximo 99 personas"
            else -> null
        }
        _uiState.update { it.copy(errores = ColaErrores(cantidad = error)) }
        return error == null
    }

    fun registrar() {
        if (!validar()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            colaRepository.registrar(_uiState.value.cantidadTexto.toInt())
            _uiState.update { it.copy(isLoading = false, cantidadTexto = "", mostrarConfirmacion = true) }
        }
    }

    fun confirmacionMostrada() {
        _uiState.update { it.copy(mostrarConfirmacion = false) }
    }

    companion object {
        fun factory(app: GestionProbadoresApplication) = viewModelFactory {
            initializer { ColaViewModel(app.container.colaRepository) }
        }
    }
}
