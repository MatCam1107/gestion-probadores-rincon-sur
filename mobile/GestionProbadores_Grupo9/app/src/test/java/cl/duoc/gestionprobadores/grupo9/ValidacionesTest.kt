package cl.duoc.gestionprobadores.grupo9

import cl.duoc.gestionprobadores.grupo9.viewmodels.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {
    @Test
    fun correoCorporativoValido_noEntregaError() {
        assertNull(Validaciones.errorCorreo("ana.rojas@rinconsur.cl"))
    }

    @Test
    fun correoExterno_entregaError() {
        assertEquals("Usa un correo @rinconsur.cl", Validaciones.errorCorreo("ana@gmail.com"))
    }

    @Test
    fun contrasenaCorta_entregaError() {
        assertEquals("Debe tener al menos 8 caracteres", Validaciones.errorContrasena("1234"))
    }
}
