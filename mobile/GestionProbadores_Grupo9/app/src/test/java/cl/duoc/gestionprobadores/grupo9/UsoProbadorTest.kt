package cl.duoc.gestionprobadores.grupo9

import cl.duoc.gestionprobadores.grupo9.repository.models.UsoProbador
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UsoProbadorTest {
    @Test
    fun salidaConMenosPrendas_detectaFaltante() {
        val uso = UsoProbador(probadorId = 1, horaIngreso = 0, horaSalida = 10 * 60_000L, prendasEntrada = 4, prendasSalida = 3)
        assertTrue(uso.tieneFaltante())
    }

    @Test
    fun usoDeDiezMinutos_noEstaExcedido() {
        val uso = UsoProbador(probadorId = 1, horaIngreso = 0, horaSalida = 10 * 60_000L, prendasEntrada = 4, prendasSalida = 4)
        assertFalse(uso.excedioTiempo())
    }
}
