package cl.duoc.gestionprobadores.grupo9.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un probador físico de la tienda.
 *
 * Es a la vez el modelo de dominio y la entidad Room (capa model/ = capa de
 * datos persistente), siguiendo el patrón simple acordado para el proyecto.
 *
 * @param numero número visible del probador (ej. "Probador 1").
 * @param estado estado actual, ver [EstadoProbador].
 * @param horaInicioOcupacion epoch millis de cuándo empezó a ocuparse, o
 *   null si está LIBRE. Se usa para calcular cuánto tiempo lleva ocupado y
 *   decidir si pasa a EXCEDIDO.
 * @param cantidadPrendas prendas que el cliente llevó al probador (dato
 *   informativo que ya traía la base de Elvira).
 */
@Entity(tableName = "probadores")
data class Probador(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numero: String,
    val estado: EstadoProbador = EstadoProbador.LIBRE,
    val horaInicioOcupacion: Long? = null,
    val cantidadPrendas: Int = 0
)
