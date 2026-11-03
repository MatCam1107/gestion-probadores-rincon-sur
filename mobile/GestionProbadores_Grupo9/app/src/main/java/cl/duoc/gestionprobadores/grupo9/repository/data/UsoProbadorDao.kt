package cl.duoc.gestionprobadores.grupo9.repository.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.gestionprobadores.grupo9.repository.models.UsoProbador
import kotlinx.coroutines.flow.Flow

@Dao
interface UsoProbadorDao {
    @Query("SELECT * FROM usos_probador ORDER BY horaSalida DESC")
    fun observarTodos(): Flow<List<UsoProbador>>

    @Insert
    suspend fun insertar(uso: UsoProbador): Long
}
