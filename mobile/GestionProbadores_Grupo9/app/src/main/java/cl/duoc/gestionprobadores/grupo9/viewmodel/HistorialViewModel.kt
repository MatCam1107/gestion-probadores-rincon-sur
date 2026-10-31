package cl.duoc.gestionprobadores.grupo9.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.gestionprobadores.grupo9.GestionProbadoresApplication
import cl.duoc.gestionprobadores.grupo9.repository.ProbadorRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Una fila de historial ya lista para pintar (con el número del probador resuelto). */
data class FilaHistorial(
    val id: Int,
    val numeroProbador: String,
    val horaInicio: Long,
    val duracionMinutos: Int?
)

data class HistorialUiState(
    val filas: List<FilaHistorial> = emptyList(),
    val promedioMinutos: Double? = null
)

/**
 * Cruza RegistroUso (de todos los probadores) con Probador para mostrar el
 * número de probador en vez del id, y calcula el promedio de minutos
 * (el promedio real lo calcula Room con SQL AVG(); acá solo se formatea).
 */
class HistorialViewModel(probadorRepository: ProbadorRepository) : ViewModel() {

    val uiState: StateFlow<HistorialUiState> = combine(
        probadorRepository.observarTodosLosUsos(),
        probadorRepository.observarProbadores(),
        probadorRepository.observarPromedioMinutos()
    ) { usos, probadores, promedio ->
        val filas = usos.map { uso ->
            FilaHistorial(
                id = uso.id,
                numeroProbador = probadores.find { it.id == uso.probadorId }?.numero
                    ?: "Probador ${uso.probadorId}",
                horaInicio = uso.horaInicio,
                duracionMinutos = uso.duracionMinutos
            )
        }
        HistorialUiState(filas = filas, promedioMinutos = promedio)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistorialUiState())

    companion object {
        fun factory(app: GestionProbadoresApplication) = viewModelFactory {
            initializer { HistorialViewModel(app.container.probadorRepository) }
        }
    }
}
