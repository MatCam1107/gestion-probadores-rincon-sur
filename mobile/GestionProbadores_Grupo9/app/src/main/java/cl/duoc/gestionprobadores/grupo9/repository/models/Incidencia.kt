package cl.duoc.gestionprobadores.grupo9.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidencias")
data class Incidencia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val probadorId: Int,
    val tipo: String,
    val descripcion: String,
    val cantidadFaltante: Int = 0,
    val fotoUri: String? = null,
    val fecha: Long = System.currentTimeMillis(),
    val reportadaPor: String,
    val resuelta: Boolean = false,
    val resueltaPor: String? = null,
    val fechaResolucion: Long? = null
) {
    companion object {
        const val PRENDA_TIENDA = "PRENDA_TIENDA"
        const val OBJETO_CLIENTE = "OBJETO_CLIENTE"
        const val PRENDAS_FALTANTES = "PRENDAS_FALTANTES"
    }
}
