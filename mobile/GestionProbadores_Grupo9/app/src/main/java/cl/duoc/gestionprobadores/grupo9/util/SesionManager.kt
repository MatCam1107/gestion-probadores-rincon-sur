package cl.duoc.gestionprobadores.grupo9.util

import cl.duoc.gestionprobadores.grupo9.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Mantiene en memoria qué usuario inició sesión. Es a propósito muy simple
 * (no hay backend ni tokens): un solo StateFlow que LoginViewModel llena al
 * autenticar y que ConfigViewModel vacía al cerrar sesión. Vive en el
 * AppContainer para que sea el mismo objeto durante toda la vida de la app.
 */
class SesionManager {
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun iniciarSesion(usuario: Usuario) {
        _usuarioActual.value = usuario
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }
}
