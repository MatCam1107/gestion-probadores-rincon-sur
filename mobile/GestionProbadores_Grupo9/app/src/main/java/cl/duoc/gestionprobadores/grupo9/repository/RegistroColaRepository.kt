package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.model.RegistroCola

class RegistroColaRepository {

    private val registros = mutableListOf(
        RegistroCola(id = 1, hora = "16:05", cantidadClientes = 6),
        RegistroCola(id = 2, hora = "15:40", cantidadClientes = 4),
        RegistroCola(id = 3, hora = "15:10", cantidadClientes = 5)
    )

    fun obtenerRegistros(): List<RegistroCola> {
        return registros.toList()
    }

    fun agregarRegistro(registro: RegistroCola) {
        registros.add(0, registro)
    }
}