package cl.duoc.gestionprobadores.grupo9.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.gestionprobadores.grupo9.GestionProbadoresApplication
import cl.duoc.gestionprobadores.grupo9.model.Alerta
import cl.duoc.gestionprobadores.grupo9.repository.AlertaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertasViewModel(private val alertaRepository: AlertaRepository) : ViewModel() {

    val alertas: StateFlow<List<Alerta>> = alertaRepository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cantidadPendientes: StateFlow<Int> = alertaRepository.observarCantidadPendientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun marcarAtendida(alerta: Alerta) {
        viewModelScope.launch { alertaRepository.marcarAtendida(alerta) }
    }

    companion object {
        fun factory(app: GestionProbadoresApplication) = viewModelFactory {
            initializer { AlertasViewModel(app.container.alertaRepository) }
        }
    }
}
