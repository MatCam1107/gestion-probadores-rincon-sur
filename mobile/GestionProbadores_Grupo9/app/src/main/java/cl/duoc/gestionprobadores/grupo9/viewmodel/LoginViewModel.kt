package cl.duoc.gestionprobadores.grupo9.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.gestionprobadores.grupo9.GestionProbadoresApplication
import cl.duoc.gestionprobadores.grupo9.model.Rol
import cl.duoc.gestionprobadores.grupo9.model.Usuario
import cl.duoc.gestionprobadores.grupo9.repository.UsuarioRepository
import cl.duoc.gestionprobadores.grupo9.util.SesionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---------- Login ----------

data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null
)

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errores: LoginErrores = LoginErrores(),
    val errorGeneral: String? = null,
    val isLoading: Boolean = false,
    val loginExitoso: Boolean = false
)

// ---------- Registro ----------

data class RegistroErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null
)

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val rol: Rol = Rol.VENDEDOR,
    val errores: RegistroErrores = RegistroErrores(),
    val errorGeneral: String? = null,
    val isLoading: Boolean = false,
    val registroExitoso: Boolean = false
)

/**
 * ViewModel compartido entre LoginScreen y RegistroScreen: ambas pantallas
 * son dos caras del mismo flujo de autenticación, así que comparten un solo
 * ViewModel (como se acordó en el diseño), cada una con su propio UiState.
 */
class LoginViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val sesionManager: SesionManager
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registroUiState = MutableStateFlow(RegistroUiState())
    val registroUiState: StateFlow<RegistroUiState> = _registroUiState.asStateFlow()

    // --- Login: cambios de campo (limpian el error de ESE campo al tipear) ---

    fun onLoginCorreoChange(valor: String) {
        _loginUiState.update { it.copy(correo = valor, errores = it.errores.copy(correo = null), errorGeneral = null) }
    }

    fun onLoginClaveChange(valor: String) {
        _loginUiState.update { it.copy(clave = valor, errores = it.errores.copy(clave = null), errorGeneral = null) }
    }

    private fun validarLogin(): Boolean {
        val estado = _loginUiState.value
        val errores = LoginErrores(
            correo = when {
                estado.correo.isBlank() -> "Ingresa tu correo"
                !estado.correo.contains("@") -> "Correo no válido"
                else -> null
            },
            clave = if (estado.clave.isBlank()) "Ingresa tu clave" else null
        )
        _loginUiState.update { it.copy(errores = errores) }
        return errores.correo == null && errores.clave == null
    }

    fun login() {
        if (!validarLogin()) return
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true, errorGeneral = null) }
            val estado = _loginUiState.value
            val usuario = usuarioRepository.login(estado.correo, estado.clave)
            if (usuario != null) {
                sesionManager.iniciarSesion(usuario)
                _loginUiState.update { it.copy(isLoading = false, loginExitoso = true) }
            } else {
                _loginUiState.update {
                    it.copy(isLoading = false, errorGeneral = "Correo o clave incorrectos")
                }
            }
        }
    }

    // --- Registro ---

    fun onRegistroNombreChange(valor: String) {
        _registroUiState.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onRegistroCorreoChange(valor: String) {
        _registroUiState.update { it.copy(correo = valor, errores = it.errores.copy(correo = null), errorGeneral = null) }
    }

    fun onRegistroClaveChange(valor: String) {
        _registroUiState.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onRegistroConfirmarClaveChange(valor: String) {
        _registroUiState.update { it.copy(confirmarClave = valor, errores = it.errores.copy(confirmarClave = null)) }
    }

    fun onRegistroRolChange(valor: Rol) {
        _registroUiState.update { it.copy(rol = valor) }
    }

    private fun validarRegistro(): Boolean {
        val estado = _registroUiState.value
        val errores = RegistroErrores(
            nombre = if (estado.nombre.isBlank()) "Ingresa tu nombre" else null,
            correo = when {
                estado.correo.isBlank() -> "Ingresa un correo"
                !estado.correo.contains("@") -> "Correo no válido"
                else -> null
            },
            clave = if (estado.clave.length < 4) "Mínimo 4 caracteres" else null,
            confirmarClave = if (estado.confirmarClave != estado.clave) "Las claves no coinciden" else null
        )
        _registroUiState.update { it.copy(errores = errores) }
        return listOf(errores.nombre, errores.correo, errores.clave, errores.confirmarClave).all { it == null }
    }

    fun registrar() {
        if (!validarRegistro()) return
        viewModelScope.launch {
            _registroUiState.update { it.copy(isLoading = true, errorGeneral = null) }
            val estado = _registroUiState.value
            val resultado = usuarioRepository.registrar(estado.nombre, estado.correo, estado.clave, estado.rol)
            resultado.onSuccess { usuario: Usuario ->
                sesionManager.iniciarSesion(usuario)
                _registroUiState.update { it.copy(isLoading = false, registroExitoso = true) }
            }.onFailure { error ->
                _registroUiState.update {
                    it.copy(isLoading = false, errorGeneral = error.message ?: "No se pudo registrar")
                }
            }
        }
    }

    companion object {
        /**
         * Fábrica explícita: recibe el Application ya casteado (se obtiene
         * una vez en el NavGraph con LocalContext.current) y arma el
         * ViewModel con las dependencias del AppContainer. Se prefiere esta
         * forma explícita, más fácil de explicar en la defensa, por sobre
         * leer el Application desde CreationExtras.
         */
        fun factory(app: GestionProbadoresApplication) = viewModelFactory {
            initializer {
                LoginViewModel(app.container.usuarioRepository, app.container.sesionManager)
            }
        }
    }
}
