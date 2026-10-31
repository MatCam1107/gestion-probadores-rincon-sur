package cl.duoc.gestionprobadores.grupo9.viewmodel

import cl.duoc.gestionprobadores.grupo9.MainDispatcherRule
import cl.duoc.gestionprobadores.grupo9.fakes.FakeUsuarioRepository
import cl.duoc.gestionprobadores.grupo9.util.SesionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun crearViewModel() = LoginViewModel(FakeUsuarioRepository(), SesionManager())

    @Test
    fun `correo sin arroba muestra error y no intenta loguear`() {
        val viewModel = crearViewModel()

        viewModel.onLoginCorreoChange("vendedorrinconsur.cl")
        viewModel.onLoginClaveChange("12345678")
        viewModel.login()

        assertEquals("Correo no válido", viewModel.loginUiState.value.errores.correo)
        assertTrue(!viewModel.loginUiState.value.loginExitoso)
    }

    @Test
    fun `credenciales correctas de la cuenta demo autentican`() {
        val viewModel = crearViewModel()

        viewModel.onLoginCorreoChange("vendedor@rinconsur.cl")
        viewModel.onLoginClaveChange("12345678")
        viewModel.login()

        assertTrue(viewModel.loginUiState.value.loginExitoso)
        assertNull(viewModel.loginUiState.value.errorGeneral)
    }

    @Test
    fun `clave incorrecta muestra error general, no error por campo`() {
        val viewModel = crearViewModel()

        viewModel.onLoginCorreoChange("vendedor@rinconsur.cl")
        viewModel.onLoginClaveChange("claveMala")
        viewModel.login()

        assertNotNull(viewModel.loginUiState.value.errorGeneral)
        assertTrue(!viewModel.loginUiState.value.loginExitoso)
    }

    @Test
    fun `registro con claves que no coinciden falla la validacion`() {
        val viewModel = crearViewModel()

        viewModel.onRegistroNombreChange("Nicolás Rojas")
        viewModel.onRegistroCorreoChange("nico@duocuc.cl")
        viewModel.onRegistroClaveChange("abcd1234")
        viewModel.onRegistroConfirmarClaveChange("otraClave")
        viewModel.registrar()

        assertEquals("Las claves no coinciden", viewModel.registroUiState.value.errores.confirmarClave)
        assertTrue(!viewModel.registroUiState.value.registroExitoso)
    }
}
