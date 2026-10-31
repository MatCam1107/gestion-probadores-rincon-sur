package cl.duoc.gestionprobadores.grupo9.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.gestionprobadores.grupo9.GestionProbadoresApplication
import cl.duoc.gestionprobadores.grupo9.data.ConfigValores
import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.Probador
import cl.duoc.gestionprobadores.grupo9.model.RegistroUso
import cl.duoc.gestionprobadores.grupo9.repository.AlertaRepository
import cl.duoc.gestionprobadores.grupo9.repository.ProbadorRepository
import cl.duoc.gestionprobadores.grupo9.util.NotificacionHelper
import cl.duoc.gestionprobadores.grupo9.util.VibracionHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel compartido por PanelScreen (grilla de probadores) y
 * DetalleScreen (ficha de un probador). También es responsable de revisar
 * periódicamente si algún probador OCUPADO superó el umbral configurado y,
 * si es así, marcarlo EXCEDIDO + generar la Alerta + vibrar + notificar.
 */
class PanelViewModel(
    private val application: Application,
    private val probadorRepository: ProbadorRepository,
    private val alertaRepository: AlertaRepository,
    configFlow: kotlinx.coroutines.flow.Flow<ConfigValores>
) : ViewModel() {

    val probadores: StateFlow<List<Probador>> = probadorRepository.observarProbadores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val config: StateFlow<ConfigValores> = configFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConfigValores())

    private val _probadorSeleccionadoId = MutableStateFlow<Int?>(null)

    val probadorSeleccionado: StateFlow<Probador?> =
        combine(probadores, _probadorSeleccionadoId) { lista, id ->
            lista.find { it.id == id }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val usosRecientes: StateFlow<List<RegistroUso>> = _probadorSeleccionadoId
        .flatMapLatest { id ->
            if (id == null) kotlinx.coroutines.flow.flowOf(emptyList())
            else probadorRepository.observarUsosPorProbador(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Reloj simple: cada 15s revisa si algún OCUPADO superó el umbral.
        viewModelScope.launch {
            while (true) {
                revisarTiempoExcedido()
                delay(15_000)
            }
        }
    }

    fun seleccionarProbador(id: Int) {
        _probadorSeleccionadoId.value = id
    }

    fun ocupar(probadorId: Int) {
        viewModelScope.launch { probadorRepository.ocupar(probadorId) }
    }

    fun marcarLibre(probadorId: Int) {
        viewModelScope.launch { probadorRepository.marcarLibre(probadorId) }
    }

    private suspend fun revisarTiempoExcedido() {
        val umbralMs = config.value.umbralMinutos * 60_000L
        val ahora = System.currentTimeMillis()

        probadores.value
            .filter { it.estado == EstadoProbador.OCUPADO && it.horaInicioOcupacion != null }
            .filter { ahora - (it.horaInicioOcupacion ?: ahora) >= umbralMs }
            .forEach { probador ->
                probadorRepository.marcarExcedido(probador.id)
                alertaRepository.generarTiempoExcedido(probador.id)

                if (config.value.vibracionActiva) {
                    VibracionHelper.vibrarAlerta(application)
                }
                if (config.value.notificacionesActivas) {
                    NotificacionHelper.mostrarAlerta(
                        context = application,
                        id = probador.id,
                        titulo = "Tiempo excedido",
                        mensaje = "${probador.numero} superó el tiempo límite"
                    )
                }
            }
    }

    companion object {
        fun factory(app: GestionProbadoresApplication) = viewModelFactory {
            initializer {
                PanelViewModel(
                    application = app,
                    probadorRepository = app.container.probadorRepository,
                    alertaRepository = app.container.alertaRepository,
                    configFlow = app.container.configPreferences.configFlow
                )
            }
        }
    }
}
