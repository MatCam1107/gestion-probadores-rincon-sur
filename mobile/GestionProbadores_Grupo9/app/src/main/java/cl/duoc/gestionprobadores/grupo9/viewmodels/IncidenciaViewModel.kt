package cl.duoc.gestionprobadores.grupo9.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionprobadores.grupo9.repository.data.AppDatabase
import cl.duoc.gestionprobadores.grupo9.repository.models.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.repository.models.Incidencia
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IncidenciaViewModel(application: Application) : AndroidViewModel(application) {
    private val bd = AppDatabase.obtener(application)
    private val dao = bd.incidenciaDao()
    private val probadorDao = bd.probadorDao()

    val incidencias: StateFlow<List<Incidencia>> = dao.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _mensajes = MutableSharedFlow<String>()
    val mensajes = _mensajes.asSharedFlow()

    fun guardar(
        probadorId: Int,
        tipo: String,
        descripcion: String,
        cantidadFaltante: Int,
        fotoUri: String?,
        colaborador: String,
        alTerminar: () -> Unit
    ) {
        val error = Validaciones.errorDescripcion(descripcion)
        if (error != null) {
            viewModelScope.launch { _mensajes.emit(error) }
            return
        }
        viewModelScope.launch {
            dao.insertar(
                Incidencia(
                    probadorId = probadorId,
                    tipo = tipo,
                    descripcion = descripcion.trim(),
                    cantidadFaltante = if (tipo == Incidencia.PRENDAS_FALTANTES) cantidadFaltante else 0,
                    fotoUri = fotoUri,
                    reportadaPor = colaborador
                )
            )
            val probador = probadorDao.buscarPorId(probadorId)
            if (probador != null && probador.horaInicio == null) {
                probadorDao.actualizar(probador.copy(estado = EstadoProbador.REVISAR))
            }
            _mensajes.emit("Incidencia registrada")
            alTerminar()
        }
    }

    fun resolver(id: Int, colaborador: String) {
        viewModelScope.launch {
            val incidencia = dao.buscarPorId(id) ?: return@launch
            dao.actualizar(
                incidencia.copy(
                    resuelta = true,
                    resueltaPor = colaborador,
                    fechaResolucion = System.currentTimeMillis()
                )
            )
            val quedan = incidencias.value.any { it.probadorId == incidencia.probadorId && !it.resuelta && it.id != id }
            val probador = probadorDao.buscarPorId(incidencia.probadorId)
            if (!quedan && probador != null && probador.horaInicio == null && probador.estado == EstadoProbador.REVISAR) {
                probadorDao.actualizar(probador.copy(estado = EstadoProbador.LIBRE))
            }
            _mensajes.emit("Incidencia resuelta")
        }
    }
}
