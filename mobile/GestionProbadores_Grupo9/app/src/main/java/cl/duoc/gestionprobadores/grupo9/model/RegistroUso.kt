package cl.duoc.gestionprobadores.grupo9.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Historial de cada vez que un probador fue usado. Se crea un registro al
 * ocupar el probador (horaFin = null, duracionMinutos = null) y se completa
 * al liberarlo. HistorialScreen usa esta tabla para calcular promedios.
 */
@Entity(tableName = "registros_uso")
data class RegistroUso(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val probadorId: Int,
    val horaInicio: Long,
    val horaFin: Long? = null,
    val duracionMinutos: Int? = null
)
