package cl.duoc.gestionprobadores.grupo9.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usos_probador")
data class UsoProbador(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val probadorId: Int,
    val horaIngreso: Long,
    val horaSalida: Long,
    val prendasEntrada: Int,
    val prendasSalida: Int
) {
    fun duracionMinutos(): Int = ((horaSalida - horaIngreso) / 60_000L).toInt().coerceAtLeast(1)
    fun tieneFaltante(): Boolean = prendasSalida < prendasEntrada
    fun excedioTiempo(): Boolean = duracionMinutos() > 15
}
