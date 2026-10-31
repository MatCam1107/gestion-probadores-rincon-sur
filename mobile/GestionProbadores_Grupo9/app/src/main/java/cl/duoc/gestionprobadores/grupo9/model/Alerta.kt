package cl.duoc.gestionprobadores.grupo9.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una alerta generada por la app (tiempo excedido o prenda olvidada).
 * AlertasScreen las lista y permite marcarlas como atendidas.
 */
@Entity(tableName = "alertas")
data class Alerta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val probadorId: Int,
    val tipo: TipoAlerta,
    val timestamp: Long,
    val atendida: Boolean = false
)
