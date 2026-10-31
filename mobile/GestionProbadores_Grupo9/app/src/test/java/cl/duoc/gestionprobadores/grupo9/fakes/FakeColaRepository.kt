package cl.duoc.gestionprobadores.grupo9.fakes

import cl.duoc.gestionprobadores.grupo9.model.RegistroCola
import cl.duoc.gestionprobadores.grupo9.repository.ColaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeColaRepository : ColaRepository {
    private val registros = MutableStateFlow<List<RegistroCola>>(emptyList())
    var ultimaCantidadRegistrada: Int? = null
        private set

    override fun observarTodos(): Flow<List<RegistroCola>> = registros
    override fun observarUltimo(): Flow<RegistroCola?> = registros.map { it.firstOrNull() }

    override suspend fun registrar(cantidadClientes: Int) {
        ultimaCantidadRegistrada = cantidadClientes
        registros.value = listOf(RegistroCola(id = registros.value.size + 1, cantidadClientes = cantidadClientes, timestamp = 0L)) + registros.value
    }
}
