package cl.duoc.gestionprobadores.grupo9.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.gestionprobadores.grupo9.model.RegistroCola
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroColaDao {

    @Insert
    suspend fun insertar(registro: RegistroCola): Long

    @Query("SELECT * FROM registros_cola ORDER BY timestamp DESC")
    fun observarTodos(): Flow<List<RegistroCola>>

    @Query("SELECT * FROM registros_cola ORDER BY timestamp DESC LIMIT 1")
    fun observarUltimo(): Flow<RegistroCola?>
}
