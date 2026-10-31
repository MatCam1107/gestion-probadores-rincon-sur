package cl.duoc.gestionprobadores.grupo9.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.gestionprobadores.grupo9.model.Probador
import kotlinx.coroutines.flow.Flow

@Dao
interface ProbadorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(probador: Probador): Long

    @Update
    suspend fun actualizar(probador: Probador)

    @Delete
    suspend fun eliminar(probador: Probador)

    @Query("SELECT * FROM probadores ORDER BY numero ASC")
    fun observarTodos(): Flow<List<Probador>>

    @Query("SELECT * FROM probadores WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Probador?

    @Query("SELECT COUNT(*) FROM probadores")
    suspend fun contar(): Int
}
