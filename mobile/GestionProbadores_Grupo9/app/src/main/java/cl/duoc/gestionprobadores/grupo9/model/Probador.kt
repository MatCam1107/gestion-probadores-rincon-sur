package cl.duoc.gestionprobadores.grupo9.model

data class Probador(
    val id: Int,
    val nombre: String,
    val estado: EstadoProbador,
    val cantidadPrendas: Int = 0
)