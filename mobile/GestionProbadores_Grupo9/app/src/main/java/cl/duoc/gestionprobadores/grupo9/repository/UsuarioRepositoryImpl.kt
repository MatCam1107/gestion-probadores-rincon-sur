package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.data.dao.UsuarioDao
import cl.duoc.gestionprobadores.grupo9.model.Rol
import cl.duoc.gestionprobadores.grupo9.model.Usuario

class UsuarioRepositoryImpl(private val usuarioDao: UsuarioDao) : UsuarioRepository {

    override suspend fun login(correo: String, clave: String): Usuario? =
        usuarioDao.autenticar(correo.trim(), clave)

    override suspend fun registrar(
        nombre: String,
        correo: String,
        clave: String,
        rol: Rol
    ): Result<Usuario> {
        val existente = usuarioDao.obtenerPorCorreo(correo.trim())
        if (existente != null) {
            return Result.failure(IllegalStateException("Ya existe una cuenta con ese correo"))
        }
        val nuevo = Usuario(nombre = nombre.trim(), correo = correo.trim(), clave = clave, rol = rol)
        val id = usuarioDao.insertar(nuevo)
        return Result.success(nuevo.copy(id = id.toInt()))
    }
}
