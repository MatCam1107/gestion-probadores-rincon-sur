package cl.duoc.gestionprobadores.grupo9.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Una lectura de "cuántos clientes están esperando", con su marca de tiempo. */
@Entity(tableName = "registros_cola")
data class RegistroCola(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cantidadClientes: Int,
    val timestamp: Long
)
