package cl.duoc.gestionprobadores.grupo9.viewmodel

import cl.duoc.gestionprobadores.grupo9.MainDispatcherRule
import cl.duoc.gestionprobadores.grupo9.fakes.FakeColaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/**
 * Prueba la validación del formulario de Cola, desacoplada de la UI
 * (vive en el ViewModel, como pide la rúbrica IE 2.1.1 de la defensa).
 */
class ColaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `cantidad vacia muestra error y no registra`() = runTest {
        val repo = FakeColaRepository()
        val viewModel = ColaViewModel(repo)

        viewModel.registrar() // sin haber escrito nada

        assertNotNull(viewModel.uiState.value.errores.cantidad)
        assertNull(repo.ultimaCantidadRegistrada)
    }

    @Test
    fun `cantidad mayor a 99 es invalida`() = runTest {
        val repo = FakeColaRepository()
        val viewModel = ColaViewModel(repo)

        viewModel.onCantidadChange("150")
        viewModel.registrar()

        assertEquals("Máximo 99 personas", viewModel.uiState.value.errores.cantidad)
        assertNull(repo.ultimaCantidadRegistrada)
    }

    @Test
    fun `cantidad valida se registra y limpia el campo`() = runTest {
        val repo = FakeColaRepository()
        val viewModel = ColaViewModel(repo)

        viewModel.onCantidadChange("7")
        viewModel.registrar()

        assertEquals(7, repo.ultimaCantidadRegistrada)
        assertEquals("", viewModel.uiState.value.cantidadTexto)
        assertNull(viewModel.uiState.value.errores.cantidad)
    }

    @Test
    fun `onCantidadChange ignora texto que no sean digitos`() {
        val viewModel = ColaViewModel(FakeColaRepository())

        viewModel.onCantidadChange("12")
        viewModel.onCantidadChange("12a") // debe ser ignorado

        assertEquals("12", viewModel.uiState.value.cantidadTexto)
    }
}
