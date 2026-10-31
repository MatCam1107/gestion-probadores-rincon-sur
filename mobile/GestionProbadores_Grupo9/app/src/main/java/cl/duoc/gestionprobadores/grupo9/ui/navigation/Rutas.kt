package cl.duoc.gestionprobadores.grupo9.ui.navigation

/** Todas las rutas del NavHost en un solo lugar, para no repetir Strings sueltos. */
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val PANEL = "panel"
    const val DETALLE = "detalle/{probadorId}"
    const val COLA = "cola"
    const val ALERTAS = "alertas"
    const val HISTORIAL = "historial"
    const val CONFIG = "config"

    fun detalle(probadorId: Int) = "detalle/$probadorId"
}

/** Pantallas principales con barra de navegación inferior (después del login). */
enum class DestinoPrincipal(val ruta: String, val etiqueta: String) {
    PANEL(Rutas.PANEL, "Panel"),
    COLA(Rutas.COLA, "Cola"),
    ALERTAS(Rutas.ALERTAS, "Alertas"),
    HISTORIAL(Rutas.HISTORIAL, "Historial"),
    CONFIG(Rutas.CONFIG, "Config")
}
