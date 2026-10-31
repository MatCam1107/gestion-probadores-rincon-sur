package cl.duoc.gestionprobadores.grupo9.util

import android.content.Context
import cl.duoc.gestionprobadores.grupo9.data.AppDatabase
import cl.duoc.gestionprobadores.grupo9.data.ConfigPreferences
import cl.duoc.gestionprobadores.grupo9.repository.AlertaRepository
import cl.duoc.gestionprobadores.grupo9.repository.AlertaRepositoryImpl
import cl.duoc.gestionprobadores.grupo9.repository.ColaRepository
import cl.duoc.gestionprobadores.grupo9.repository.ColaRepositoryImpl
import cl.duoc.gestionprobadores.grupo9.repository.ProbadorRepository
import cl.duoc.gestionprobadores.grupo9.repository.ProbadorRepositoryImpl
import cl.duoc.gestionprobadores.grupo9.repository.ReportePrendaRepository
import cl.duoc.gestionprobadores.grupo9.repository.ReportePrendaRepositoryImpl
import cl.duoc.gestionprobadores.grupo9.repository.UsuarioRepository
import cl.duoc.gestionprobadores.grupo9.repository.UsuarioRepositoryImpl

/**
 * "Contenedor" manual de dependencias: arma la base de datos Room una sola
 * vez y entrega los Repository ya construidos. Reemplaza a un framework de
 * inyección de dependencias (Hilt/Koin) que para el alcance de este proyecto
 * sería complejidad innecesaria; así cada ViewModel sigue dependiendo solo
 * de una interfaz de Repository (fácil de explicar y de reemplazar por un
 * Fake en tests).
 */
class AppContainer(context: Context) {

    private val database: AppDatabase = AppDatabase.getDatabase(context)

    val probadorRepository: ProbadorRepository by lazy {
        ProbadorRepositoryImpl(database.probadorDao(), database.registroUsoDao())
    }

    val alertaRepository: AlertaRepository by lazy {
        AlertaRepositoryImpl(database.alertaDao())
    }

    val colaRepository: ColaRepository by lazy {
        ColaRepositoryImpl(database.registroColaDao())
    }

    val reportePrendaRepository: ReportePrendaRepository by lazy {
        ReportePrendaRepositoryImpl(database.reportePrendaDao())
    }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepositoryImpl(database.usuarioDao())
    }

    val configPreferences: ConfigPreferences by lazy {
        ConfigPreferences(context.applicationContext)
    }

    val sesionManager: SesionManager by lazy { SesionManager() }
}
