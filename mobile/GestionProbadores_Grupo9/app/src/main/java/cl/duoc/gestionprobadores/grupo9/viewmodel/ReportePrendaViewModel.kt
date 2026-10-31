package cl.duoc.gestionprobadores.grupo9.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.gestionprobadores.grupo9.GestionProbadoresApplication
import cl.duoc.gestionprobadores.grupo9.repository.AlertaRepository
import cl.duoc.gestionprobadores.grupo9.repository.ReportePrendaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportePrendaErrores(
    val descripcion: String? = null
)

data class ReportePrendaUiState(
    val probadorId: Int? = null,
    val descripcion: String = "",
    val fotoUri: String? = null,
    val errores: ReportePrendaErrores = ReportePrendaErrores(),
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false
)

/**
 * ViewModel propio del formulario "Prenda olvidada" (vive dentro de
 * DetalleScreen). Mantiene su propio UiState + validación desacoplada de la
 * UI, siguiendo el mismo patrón que LoginViewModel y ColaViewModel.
 */
class ReportePrendaViewModel(
    private val reportePrendaRepository: ReportePrendaRepository,
    private val alertaRepository: AlertaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportePrendaUiState())
    val uiState: StateFlow<ReportePrendaUiState> = _uiState.asStateFlow()

    fun inicializar(probadorId: Int) {
        _uiState.update { it.copy(probadorId = probadorId) }
    }

    fun onDescripcionChange(valor: String) {
        _uiState.update { it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null)) }
    }

    fun onFotoCapturada(uri: String?) {
        _uiState.update { it.copy(fotoUri = uri) }
    }

    private fun validar(): Boolean {
        val estado = _uiState.value
        val errores = ReportePrendaErrores(
            descripcion = if (estado.descripcion.isBlank()) "Describe la prenda encontrada" else null
        )
        _uiState.update { it.copy(errores = errores) }
        return errores.descripcion == null
    }

    fun guardar() {
        if (!validar()) return
        val estado = _uiState.value
        val probadorId = estado.probadorId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            reportePrendaRepository.registrar(probadorId, estado.descripcion, estado.fotoUri)
            alertaRepository.generarPrendaOlvidada(probadorId)
            _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
        }
    }

    fun limpiar() {
        _uiState.value = ReportePrendaUiState(probadorId = _uiState.value.probadorId)
    }

    companion object {
        fun factory(app: GestionProbadoresApplication) = viewModelFactory {
            initializer {
                ReportePrendaViewModel(
                    app.container.reportePrendaRepository,
                    app.container.alertaRepository
                )
            }
        }
    }
}
