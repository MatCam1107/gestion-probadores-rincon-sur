package cl.duoc.gestionprobadores.grupo9.repository

import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.Probador
import cl.duoc.gestionprobadores.grupo9.model.RegistroUso
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acceso a datos de probadores y su historial de uso.
 * El ViewModel programa contra esta interfaz, nunca contra Room directamente.
 */
interface ProbadorRepository {

    fun observarProbadores(): Flow<List<Probador>>

    fun observarUsosPorProbador(probadorId: Int): Flow<List<RegistroUso>>

    /** Historial completo (de todos los probadores), para HistorialScreen. */
    fun observarTodosLosUsos(): Flow<List<RegistroUso>>

    fun observarPromedioMinutos(): Flow<Double?>

    /** Marca el probador como OCUPADO y abre un RegistroUso nuevo. */
    suspend fun ocupar(probadorId: Int)

    /** Marca el probador como LIBRE y cierra el RegistroUso abierto. */
    suspend fun marcarLibre(probadorId: Int)

    /** Marca el probador como EXCEDIDO (superó el umbral de minutos). */
    suspend fun marcarExcedido(probadorId: Int)

    suspend fun obtenerPorId(probadorId: Int): Probador?
}
