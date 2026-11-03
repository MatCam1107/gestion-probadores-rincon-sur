package cl.duoc.gestionprobadores.grupo9.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionprobadores.grupo9.repository.data.AppDatabase
import cl.duoc.gestionprobadores.grupo9.repository.models.Usuario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "ana.rojas@rinconsur.cl",
    val contrasena: String = "12345678",
    val cargando: Boolean = false,
    val error: String? = null,
    val usuario: Usuario? = null,
    val sucursalSeleccionada: String = "Plaza Norte",
    val sucursalesDisponibles: List<String> = listOf("Plaza Norte")
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.obtener(application).usuarioDao()
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun cambiarEmail(valor: String) {
        _uiState.update { it.copy(email = valor, error = null, sucursalesDisponibles = emptyList()) }
        viewModelScope.launch {
            val usuario = dao.buscarPorEmail(valor.trim())
            val sucursales = if (usuario != null && !usuario.esAdministrador()) usuario.listaSucursales() else emptyList()
            _uiState.update {
                it.copy(
                    sucursalesDisponibles = sucursales,
                    sucursalSeleccionada = sucursales.firstOrNull() ?: it.sucursalSeleccionada
                )
            }
        }
    }
    fun cambiarContrasena(valor: String) = _uiState.update { it.copy(contrasena = valor, error = null) }
    fun seleccionarSucursal(valor: String) = _uiState.update { it.copy(sucursalSeleccionada = valor) }

    fun usarCuenta(email: String) {
        cambiarEmail(email)
        _uiState.update { it.copy(contrasena = "12345678", error = null) }
    }

    fun iniciarSesion() {
        val estado = _uiState.value
        val error = Validaciones.errorCorreo(estado.email)
            ?: Validaciones.errorContrasena(estado.contrasena)
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            delay(500)
            val usuario = dao.iniciarSesion(estado.email.trim(), estado.contrasena)
            val mensaje = when {
                usuario == null -> "Correo o contraseña incorrectos"
                !usuario.activo -> "Esta cuenta está desactivada"
                else -> null
            }
            if (mensaje != null) {
                _uiState.update { it.copy(cargando = false, error = mensaje) }
            } else {
                val usuarioValido = requireNotNull(usuario)
                val sucursal = if (usuarioValido.esAdministrador()) "Todas" else
                    estado.sucursalSeleccionada.takeIf { it in usuarioValido.listaSucursales() }
                        ?: usuarioValido.listaSucursales().first()
                _uiState.update { it.copy(cargando = false, usuario = usuarioValido, sucursalSeleccionada = sucursal, contrasena = "") }
            }
        }
    }

    fun cerrarSesion() {
        _uiState.value = LoginUiState()
    }
}
