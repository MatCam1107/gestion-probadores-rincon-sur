package cl.duoc.gestionprobadores.grupo9.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Reporte de una prenda olvidada en un probador, con foto opcional tomada
 * con la cámara (o elegida de galería) y guardada vía FileProvider.
 */
@Entity(tableName = "reportes_prenda")
data class ReportePrenda(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val probadorId: Int,
    val descripcion: String,
    val fotoUri: String? = null,
    val timestamp: Long
)
