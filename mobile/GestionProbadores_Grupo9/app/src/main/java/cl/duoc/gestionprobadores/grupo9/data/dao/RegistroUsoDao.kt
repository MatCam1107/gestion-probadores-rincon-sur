package cl.duoc.gestionprobadores.grupo9.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.gestionprobadores.grupo9.model.RegistroUso
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroUsoDao {

    @Insert
    suspend fun insertar(registro: RegistroUso): Long

    @Update
    suspend fun actualizar(registro: RegistroUso)

    @Query("SELECT * FROM registros_uso WHERE probadorId = :probadorId ORDER BY horaInicio DESC")
    fun observarPorProbador(probadorId: Int): Flow<List<RegistroUso>>

    @Query("SELECT * FROM registros_uso ORDER BY horaInicio DESC")
    fun observarTodos(): Flow<List<RegistroUso>>

    @Query("SELECT * FROM registros_uso WHERE probadorId = :probadorId AND horaFin IS NULL LIMIT 1")
    suspend fun obtenerUsoAbierto(probadorId: Int): RegistroUso?

    @Query("SELECT AVG(duracionMinutos) FROM registros_uso WHERE duracionMinutos IS NOT NULL")
    fun observarPromedioMinutos(): Flow<Double?>
}
