package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.data.dao.RegistroColaDao
import cl.duoc.gestionprobadores.grupo9.model.RegistroCola
import kotlinx.coroutines.flow.Flow

class ColaRepositoryImpl(private val registroColaDao: RegistroColaDao) : ColaRepository {

    override fun observarTodos(): Flow<List<RegistroCola>> = registroColaDao.observarTodos()

    override fun observarUltimo(): Flow<RegistroCola?> = registroColaDao.observarUltimo()

    override suspend fun registrar(cantidadClientes: Int) {
        registroColaDao.insertar(
            RegistroCola(cantidadClientes = cantidadClientes, timestamp = System.currentTimeMillis())
        )
    }
}
