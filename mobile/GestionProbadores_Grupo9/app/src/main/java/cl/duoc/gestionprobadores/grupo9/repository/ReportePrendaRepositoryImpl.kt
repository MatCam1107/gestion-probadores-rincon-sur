package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.data.dao.ReportePrendaDao
import cl.duoc.gestionprobadores.grupo9.model.ReportePrenda
import kotlinx.coroutines.flow.Flow

class ReportePrendaRepositoryImpl(
    private val reportePrendaDao: ReportePrendaDao
) : ReportePrendaRepository {

    override fun observarPorProbador(probadorId: Int): Flow<List<ReportePrenda>> =
        reportePrendaDao.observarPorProbador(probadorId)

    override fun observarTodos(): Flow<List<ReportePrenda>> = reportePrendaDao.observarTodos()

    override suspend fun registrar(probadorId: Int, descripcion: String, fotoUri: String?) {
        reportePrendaDao.insertar(
            ReportePrenda(
                probadorId = probadorId,
                descripcion = descripcion,
                fotoUri = fotoUri,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
