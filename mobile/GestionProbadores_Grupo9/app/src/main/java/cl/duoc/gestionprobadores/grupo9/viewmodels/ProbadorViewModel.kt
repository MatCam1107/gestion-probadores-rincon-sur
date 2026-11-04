package cl.duoc.gestionprobadores.grupo9.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionprobadores.grupo9.repository.data.AppDatabase
import cl.duoc.gestionprobadores.grupo9.repository.models.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.repository.models.Incidencia
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import cl.duoc.gestionprobadores.grupo9.repository.models.UsoProbador
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ProbadorViewModel(application: Application) : AndroidViewModel(application) {
    private val bd = AppDatabase.obtener(application)
    private val probadorDao = bd.probadorDao()
    private val usoDao = bd.usoProbadorDao()
    private val incidenciaDao = bd.incidenciaDao()

    val probadores: StateFlow<List<Probador>> = probadorDao.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val usos: StateFlow<List<UsoProbador>> = usoDao.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _colas = MutableStateFlow(
        mapOf("Plaza Norte" to 3, "Mall Vespucio" to 2, "Mall Tobalaba" to 0, "Costanera Center" to 4)
    )
    val colas: StateFlow<Map<String, Int>> = _colas.asStateFlow()

    private val _ahora = MutableStateFlow(System.currentTimeMillis())
    val ahora: StateFlow<Long> = _ahora.asStateFlow()

    private val _mensajes = MutableSharedFlow<String>()
    val mensajes = _mensajes.asSharedFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                _ahora.value = System.currentTimeMillis()
                actualizarEstadosPorTiempo()
                delay(1_000)
            }
        }
    }

    private suspend fun actualizarEstadosPorTiempo() {
        probadores.value.filter { it.horaInicio != null }.forEach { probador ->
            val excedido = _ahora.value - probador.horaInicio!! >= LIMITE_MILISEGUNDOS
            if (excedido && probador.estado != EstadoProbador.EXCEDIDO) {
                probadorDao.actualizar(probador.copy(estado = EstadoProbador.EXCEDIDO))
            }
        }
    }

    fun cantidadCola(sucursal: String): Int = _colas.value[sucursal] ?: 0

    fun cambiarCola(sucursal: String, diferencia: Int) {
        _colas.update { actual ->
            actual + (sucursal to ((actual[sucursal] ?: 0) + diferencia).coerceIn(0, 50))
        }
    }

    fun registrarIngreso(probadorId: Int, prendas: Int, sucursal: String) {
        viewModelScope.launch {
            val probador = probadorDao.buscarPorId(probadorId) ?: return@launch
            if (probador.estado != EstadoProbador.LIBRE) return@launch
            probadorDao.actualizar(
                probador.copy(
                    estado = EstadoProbador.OCUPADO,
                    horaInicio = System.currentTimeMillis(),
                    cantidadPrendas = prendas.coerceIn(1, 10),
                    motivoClausura = null
                )
            )
            if (cantidadCola(sucursal) > 0) cambiarCola(sucursal, -1)
            _mensajes.emit("Ingreso registrado con $prendas prendas")
        }
    }

    fun registrarSalida(probadorId: Int, prendasSalida: Int, colaborador: String) {
        viewModelScope.launch {
            val probador = probadorDao.buscarPorId(probadorId) ?: return@launch
            val inicio = probador.horaInicio ?: return@launch
            val salida = prendasSalida.coerceIn(0, probador.cantidadPrendas)
            val fin = System.currentTimeMillis()
            usoDao.insertar(
                UsoProbador(
                    probadorId = probador.id,
                    horaIngreso = inicio,
                    horaSalida = fin,
                    prendasEntrada = probador.cantidadPrendas,
                    prendasSalida = salida
                )
            )
            val faltantes = probador.cantidadPrendas - salida
            if (faltantes > 0) {
                incidenciaDao.insertar(
                    Incidencia(
                        probadorId = probador.id,
                        tipo = Incidencia.PRENDAS_FALTANTES,
                        descripcion = "Entraron ${probador.cantidadPrendas} prendas y salieron $salida.",
                        cantidadFaltante = faltantes,
                        reportadaPor = colaborador
                    )
                )
            }
            probadorDao.actualizar(
                probador.copy(
                    estado = if (faltantes > 0) EstadoProbador.REVISAR else EstadoProbador.LIBRE,
                    horaInicio = null,
                    cantidadPrendas = 0
                )
            )
            _mensajes.emit(if (faltantes > 0) "Faltan $faltantes prendas: probador en Revisar" else "Probador liberado")
        }
    }

    fun clausurar(probadorId: Int, motivo: String) {
        viewModelScope.launch {
            val probador = probadorDao.buscarPorId(probadorId) ?: return@launch
            probadorDao.actualizar(
                probador.copy(
                    estado = EstadoProbador.CLAUSURADO,
                    horaInicio = null,
                    cantidadPrendas = 0,
                    motivoClausura = motivo
                )
            )
            _mensajes.emit("Probador clausurado: $motivo")
        }
    }

    fun habilitar(probadorId: Int) {
        viewModelScope.launch {
            val probador = probadorDao.buscarPorId(probadorId) ?: return@launch
            probadorDao.actualizar(probador.copy(estado = EstadoProbador.LIBRE, motivoClausura = null))
            _mensajes.emit("Probador habilitado")
        }
    }

    companion object {
        const val LIMITE_MILISEGUNDOS = 15 * 60 * 1_000L
    }
}
