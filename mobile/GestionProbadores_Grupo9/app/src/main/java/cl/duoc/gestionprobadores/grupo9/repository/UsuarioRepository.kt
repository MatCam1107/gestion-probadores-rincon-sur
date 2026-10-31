package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.model.Rol
import cl.duoc.gestionprobadores.grupo9.model.Usuario

interface UsuarioRepository {
    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun registrar(nombre: String, correo: String, clave: String, rol: Rol): Result<Usuario>
}
