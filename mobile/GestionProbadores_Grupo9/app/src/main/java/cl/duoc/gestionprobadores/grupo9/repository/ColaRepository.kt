package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.model.RegistroCola
import kotlinx.coroutines.flow.Flow

interface ColaRepository {
    fun observarTodos(): Flow<List<RegistroCola>>
    fun observarUltimo(): Flow<RegistroCola?>
    suspend fun registrar(cantidadClientes: Int)
}
