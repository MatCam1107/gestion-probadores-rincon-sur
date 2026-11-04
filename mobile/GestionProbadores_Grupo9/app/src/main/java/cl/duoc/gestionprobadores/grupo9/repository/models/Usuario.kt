package cl.duoc.gestionprobadores.grupo9.repository.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios", indices = [Index(value = ["email"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val rol: String = ROL_COLABORADOR,
    val sucursales: String,
    val activo: Boolean = true
) {
    fun listaSucursales(): List<String> = sucursales.split("|").filter { it.isNotBlank() }
    fun esAdministrador(): Boolean = rol == ROL_ADMINISTRADOR

    companion object {
        const val ROL_ADMINISTRADOR = "ADMINISTRADOR"
        const val ROL_COLABORADOR = "COLABORADOR"
    }
}
