package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.model.Alerta
import cl.duoc.gestionprobadores.grupo9.model.TipoAlerta
import kotlinx.coroutines.flow.Flow

interface AlertaRepository {
    fun observarTodas(): Flow<List<Alerta>>
    fun observarCantidadPendientes(): Flow<Int>

    /** Crea la alerta solo si no existe ya una de tiempo excedido sin atender para ese probador. */
    suspend fun generarTiempoExcedido(probadorId: Int)

    suspend fun generarPrendaOlvidada(probadorId: Int)

    suspend fun marcarAtendida(alerta: Alerta)
}
