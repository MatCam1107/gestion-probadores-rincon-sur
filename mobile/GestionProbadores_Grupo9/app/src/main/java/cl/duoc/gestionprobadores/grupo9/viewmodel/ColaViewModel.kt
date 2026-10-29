package cl.duoc.gestionprobadores.grupo9.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.RegistroCola
import cl.duoc.gestionprobadores.grupo9.repository.ProbadorRepository
import cl.duoc.gestionprobadores.grupo9.repository.RegistroColaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ColaViewModel(
    private val registroRepository: RegistroColaRepository = RegistroColaRepository(),
    private val probadorRepository: ProbadorRepository = ProbadorRepository()
) : ViewModel() {

    private val _cantidadClientes = MutableStateFlow(6)
    val cantidadClientes: StateFlow<Int> = _cantidadClientes

    private val _probadoresLibres = MutableStateFlow(
        probadorRepository.obtenerProbadores().count {
            it.estado == EstadoProbador.DISPONIBLE
        }
    )
    val probadoresLibres: StateFlow<Int> = _probadoresLibres

    private val _tiempoEstimado = MutableStateFlow(
        calcularTiempoEstimado(_cantidadClientes.value)
    )
    val tiempoEstimado: StateFlow<Int> = _tiempoEstimado

    private val _registros = MutableStateFlow(
        registroRepository.obtenerRegistros()
    )
    val registros: StateFlow<List<RegistroCola>> = _registros

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun aumentarClientes() {
        if (_cantidadClientes.value < 99) {
            _cantidadClientes.value++
            actualizarTiempoEstimado()
            _mensaje.value = null
        } else {
            _mensaje.value = "La cantidad máxima es 99"
        }
    }

    fun disminuirClientes() {
        if (_cantidadClientes.value > 0) {
            _cantidadClientes.value--
            actualizarTiempoEstimado()
            _mensaje.value = null
        } else {
            _mensaje.value = "La cantidad no puede ser negativa"
        }
    }

    fun guardarRegistro() {
        val cantidad = _cantidadClientes.value

        if (cantidad == 0) {
            _mensaje.value = "Debe existir al menos un cliente en espera"
            return
        }

        val nuevoId = (_registros.value.maxOfOrNull { it.id } ?: 0) + 1
        val horaActual = SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date())

        val nuevoRegistro = RegistroCola(
            id = nuevoId,
            hora = horaActual,
            cantidadClientes = cantidad
        )

        registroRepository.agregarRegistro(nuevoRegistro)
        _registros.value = registroRepository.obtenerRegistros()
        _mensaje.value = "Registro guardado correctamente"
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    private fun actualizarTiempoEstimado() {
        _tiempoEstimado.value =
            calcularTiempoEstimado(_cantidadClientes.value)
    }

    private fun calcularTiempoEstimado(cantidadClientes: Int): Int {
        val cantidadLibres = _probadoresLibres.value

        if (cantidadClientes == 0 || cantidadLibres == 0) {
            return 0
        }

        val minutosPromedioUso = 10

        return (
                cantidadClientes * minutosPromedioUso +
                        cantidadLibres - 1
                ) / cantidadLibres
    }
}