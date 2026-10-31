package cl.duoc.gestionprobadores.grupo9.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Usuario del personal de tienda. La clave se guarda en texto plano porque
 * el proyecto usa datos ficticios y login simulado (no hay backend real);
 * en una app de producción se debería hashear.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val rol: Rol
)
