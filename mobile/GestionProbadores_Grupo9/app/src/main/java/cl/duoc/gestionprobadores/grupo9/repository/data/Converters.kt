package cl.duoc.gestionprobadores.grupo9.repository.data

import androidx.room.TypeConverter
import cl.duoc.gestionprobadores.grupo9.repository.models.EstadoProbador

class Converters {
    @TypeConverter
    fun estadoAString(estado: EstadoProbador): String = estado.name

    @TypeConverter
    fun stringAEstado(valor: String): EstadoProbador = EstadoProbador.valueOf(valor)
}
