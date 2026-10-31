package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.data.dao.ProbadorDao
import cl.duoc.gestionprobadores.grupo9.data.dao.RegistroUsoDao
import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.Probador
import cl.duoc.gestionprobadores.grupo9.model.RegistroUso
import kotlinx.coroutines.flow.Flow

/** Implementación real con Room. Un probador y su RegistroUso "abierto" van de la mano. */
class ProbadorRepositoryImpl(
    private val probadorDao: ProbadorDao,
    private val registroUsoDao: RegistroUsoDao
) : ProbadorRepository {

    override fun observarProbadores(): Flow<List<Probador>> = probadorDao.observarTodos()

    override fun observarUsosPorProbador(probadorId: Int): Flow<List<RegistroUso>> =
        registroUsoDao.observarPorProbador(probadorId)

    override fun observarTodosLosUsos(): Flow<List<RegistroUso>> = registroUsoDao.observarTodos()

    override fun observarPromedioMinutos(): Flow<Double?> = registroUsoDao.observarPromedioMinutos()

    override suspend fun ocupar(probadorId: Int) {
        val probador = probadorDao.obtenerPorId(probadorId) ?: return
        val ahora = System.currentTimeMillis()
        probadorDao.actualizar(
            probador.copy(estado = EstadoProbador.OCUPADO, horaInicioOcupacion = ahora)
        )
        registroUsoDao.insertar(RegistroUso(probadorId = probadorId, horaInicio = ahora))
    }

    override suspend fun marcarLibre(probadorId: Int) {
        val probador = probadorDao.obtenerPorId(probadorId) ?: return
        val ahora = System.currentTimeMillis()

        val usoAbierto = registroUsoDao.obtenerUsoAbierto(probadorId)
        if (usoAbierto != null) {
            val duracionMin = ((ahora - usoAbierto.horaInicio) / 60000L).toInt()
            registroUsoDao.actualizar(
                usoAbierto.copy(horaFin = ahora, duracionMinutos = duracionMin)
            )
        }

        probadorDao.actualizar(
            probador.copy(estado = EstadoProbador.LIBRE, horaInicioOcupacion = null)
        )
    }

    override suspend fun marcarExcedido(probadorId: Int) {
        val probador = probadorDao.obtenerPorId(probadorId) ?: return
        if (probador.estado == EstadoProbador.OCUPADO) {
            probadorDao.actualizar(probador.copy(estado = EstadoProbador.EXCEDIDO))
        }
    }

    override suspend fun obtenerPorId(probadorId: Int): Probador? = probadorDao.obtenerPorId(probadorId)
}
