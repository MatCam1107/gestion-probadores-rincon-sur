package cl.duoc.gestionprobadores.grupo9.view.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatearDuracion(milisegundos: Long): String {
    val segundos = (milisegundos / 1_000L).coerceAtLeast(0)
    return "%02d:%02d".format(segundos / 60, segundos % 60)
}

fun formatearHora(milisegundos: Long): String =
    SimpleDateFormat("HH:mm", Locale("es", "CL")).format(Date(milisegundos))

fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("dd/MM HH:mm", Locale("es", "CL")).format(Date(milisegundos))
