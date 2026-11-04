package cl.duoc.gestionprobadores.grupo9.repository.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import kotlinx.coroutines.flow.Flow

@Dao
interface ProbadorDao {
    @Query("SELECT * FROM probadores ORDER BY sucursal, numero")
    fun observarTodos(): Flow<List<Probador>>

    @Query("SELECT * FROM probadores WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Probador?

    @Update
    suspend fun actualizar(probador: Probador)
}
