package cl.duoc.gestionprobadores.grupo9.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.gestionprobadores.grupo9.model.Alerta
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {

    @Insert
    suspend fun insertar(alerta: Alerta): Long

    @Update
    suspend fun actualizar(alerta: Alerta)

    @Query("SELECT * FROM alertas ORDER BY timestamp DESC")
    fun observarTodas(): Flow<List<Alerta>>

    @Query("SELECT * FROM alertas WHERE probadorId = :probadorId AND tipo = 'TIEMPO_EXCEDIDO' AND atendida = 0 LIMIT 1")
    suspend fun obtenerAlertaExcedidoActiva(probadorId: Int): Alerta?

    @Query("SELECT COUNT(*) FROM alertas WHERE atendida = 0")
    fun observarCantidadPendientes(): Flow<Int>
}
