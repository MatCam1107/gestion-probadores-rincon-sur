package cl.duoc.gestionprobadores.grupo9.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.gestionprobadores.grupo9.model.ReportePrenda
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportePrendaDao {

    @Insert
    suspend fun insertar(reporte: ReportePrenda): Long

    @Query("SELECT * FROM reportes_prenda WHERE probadorId = :probadorId ORDER BY timestamp DESC")
    fun observarPorProbador(probadorId: Int): Flow<List<ReportePrenda>>

    @Query("SELECT * FROM reportes_prenda ORDER BY timestamp DESC")
    fun observarTodos(): Flow<List<ReportePrenda>>
}
