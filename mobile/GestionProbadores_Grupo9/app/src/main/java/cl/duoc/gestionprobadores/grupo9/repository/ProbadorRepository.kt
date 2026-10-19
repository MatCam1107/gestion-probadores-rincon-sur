package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.Probador

class ProbadorRepository {

    private val probadores = listOf(
        Probador(1, "Probador 1", EstadoProbador.DISPONIBLE),
        Probador(2, "Probador 2", EstadoProbador.OCUPADO, 3),
        Probador(3, "Probador 3", EstadoProbador.CON_INCIDENCIA),
        Probador(4, "Probador 4", EstadoProbador.DISPONIBLE)
    )

    fun obtenerProbadores(): List<Probador> {
        return probadores
    }
}