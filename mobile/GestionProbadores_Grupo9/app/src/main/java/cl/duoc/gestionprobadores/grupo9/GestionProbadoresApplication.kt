package cl.duoc.gestionprobadores.grupo9

import android.app.Application
import cl.duoc.gestionprobadores.grupo9.util.AppContainer
import cl.duoc.gestionprobadores.grupo9.util.NotificacionHelper

/**
 * Application personalizada: crea el AppContainer una sola vez por proceso
 * y lo deja disponible para que cada ViewModelFactory lo tome desde acá
 * (ver viewModelFactory { initializer { ... } } en cada ViewModel).
 */
class GestionProbadoresApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // El canal se crea una vez al arrancar; el permiso en tiempo de
        // ejecución (Android 13+) se pide aparte, desde PanelScreen.
        NotificacionHelper.crearCanal(this)
    }
}
