package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.data.dao.AlertaDao
import cl.duoc.gestionprobadores.grupo9.model.Alerta
import cl.duoc.gestionprobadores.grupo9.model.TipoAlerta
import kotlinx.coroutines.flow.Flow

class AlertaRepositoryImpl(private val alertaDao: AlertaDao) : AlertaRepository {

    override fun observarTodas(): Flow<List<Alerta>> = alertaDao.observarTodas()

    override fun observarCantidadPendientes(): Flow<Int> = alertaDao.observarCantidadPendientes()

    override suspend fun generarTiempoExcedido(probadorId: Int) {
        val yaExiste = alertaDao.obtenerAlertaExcedidoActiva(probadorId) != null
        if (!yaExiste) {
            alertaDao.insertar(
                Alerta(
                    probadorId = probadorId,
                    tipo = TipoAlerta.TIEMPO_EXCEDIDO,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun generarPrendaOlvidada(probadorId: Int) {
        alertaDao.insertar(
            Alerta(
                probadorId = probadorId,
                tipo = TipoAlerta.PRENDA_OLVIDADA,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun marcarAtendida(alerta: Alerta) {
        alertaDao.actualizar(alerta.copy(atendida = true))
    }
}
