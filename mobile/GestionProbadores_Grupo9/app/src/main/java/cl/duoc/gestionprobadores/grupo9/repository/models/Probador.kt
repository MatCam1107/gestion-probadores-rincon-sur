package cl.duoc.gestionprobadores.grupo9.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "probadores")
data class Probador(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sucursal: String,
    val numero: Int,
    val estado: EstadoProbador = EstadoProbador.LIBRE,
    val horaInicio: Long? = null,
    val cantidadPrendas: Int = 0,
    val motivoClausura: String? = null
)
