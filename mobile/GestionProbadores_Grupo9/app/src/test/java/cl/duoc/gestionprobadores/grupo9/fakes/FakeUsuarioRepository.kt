package cl.duoc.gestionprobadores.grupo9.fakes

import cl.duoc.gestionprobadores.grupo9.model.Rol
import cl.duoc.gestionprobadores.grupo9.model.Usuario
import cl.duoc.gestionprobadores.grupo9.repository.UsuarioRepository

/** Repositorio en memoria para tests: sin Room, sin Android. */
class FakeUsuarioRepository : UsuarioRepository {
    private val usuarios = mutableListOf(
        Usuario(id = 1, nombre = "Vendedor Demo", correo = "vendedor@rinconsur.cl", clave = "12345678", rol = Rol.VENDEDOR)
    )

    override suspend fun login(correo: String, clave: String): Usuario? =
        usuarios.find { it.correo == correo && it.clave == clave }

    override suspend fun registrar(nombre: String, correo: String, clave: String, rol: Rol): Result<Usuario> {
        if (usuarios.any { it.correo == correo }) {
            return Result.failure(IllegalStateException("Ya existe una cuenta con ese correo"))
        }
        val nuevo = Usuario(id = usuarios.size + 1, nombre = nombre, correo = correo, clave = clave, rol = rol)
        usuarios.add(nuevo)
        return Result.success(nuevo)
    }
}
