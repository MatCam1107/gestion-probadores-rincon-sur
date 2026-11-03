package cl.duoc.gestionprobadores.grupo9.repository.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.gestionprobadores.grupo9.repository.models.Incidencia
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidenciaDao {
    @Query("SELECT * FROM incidencias ORDER BY resuelta, fecha DESC")
    fun observarTodas(): Flow<List<Incidencia>>

    @Query("SELECT * FROM incidencias WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Incidencia?

    @Insert
    suspend fun insertar(incidencia: Incidencia): Long

    @Update
    suspend fun actualizar(incidencia: Incidencia)
}
