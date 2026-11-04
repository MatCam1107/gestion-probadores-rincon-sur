package cl.duoc.gestionprobadores.grupo9.viewmodels

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionprobadores.grupo9.repository.data.AppDatabase
import cl.duoc.gestionprobadores.grupo9.repository.models.Usuario
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.obtener(application).usuarioDao()
    val usuarios: StateFlow<List<Usuario>> = dao.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _mensajes = MutableSharedFlow<String>()
    val mensajes = _mensajes.asSharedFlow()

    suspend fun buscar(id: Int): Usuario? = dao.buscarPorId(id)

    fun guardar(
        id: Int,
        nombre: String,
        email: String,
        contrasena: String,
        sucursales: List<String>,
        activo: Boolean,
        alTerminar: () -> Unit
    ) {
        val existente = usuarios.value.firstOrNull { it.id == id }
        val error = Validaciones.errorNombre(nombre)
            ?: Validaciones.errorCorreo(email)
            ?: Validaciones.errorContrasena(contrasena, obligatoria = id == 0)
            ?: if (sucursales.isEmpty()) "Selecciona al menos una sucursal" else null
        if (error != null) {
            viewModelScope.launch { _mensajes.emit(error) }
            return
        }
        viewModelScope.launch {
            try {
                val usuario = Usuario(
                    id = id,
                    nombre = nombre.trim(),
                    email = email.trim().lowercase(),
                    contrasena = contrasena.ifBlank { existente?.contrasena.orEmpty() },
                    rol = Usuario.ROL_COLABORADOR,
                    sucursales = sucursales.joinToString("|"),
                    activo = activo
                )
                if (id == 0) dao.insertar(usuario) else dao.actualizar(usuario)
                _mensajes.emit(if (id == 0) "Cuenta creada" else "Cambios guardados")
                alTerminar()
            } catch (_: SQLiteConstraintException) {
                _mensajes.emit("Ya existe una cuenta con ese correo")
            }
        }
    }

    fun cambiarEstado(usuario: Usuario) {
        viewModelScope.launch { dao.actualizar(usuario.copy(activo = !usuario.activo)) }
    }
}
