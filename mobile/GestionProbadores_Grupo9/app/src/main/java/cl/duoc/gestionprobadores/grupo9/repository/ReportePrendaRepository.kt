package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.model.ReportePrenda
import kotlinx.coroutines.flow.Flow

interface ReportePrendaRepository {
    fun observarPorProbador(probadorId: Int): Flow<List<ReportePrenda>>
    fun observarTodos(): Flow<List<ReportePrenda>>
    suspend fun registrar(probadorId: Int, descripcion: String, fotoUri: String?)
}
