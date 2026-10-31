package cl.duoc.gestionprobadores.grupo9.data

import androidx.room.TypeConverter
import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.Rol
import cl.duoc.gestionprobadores.grupo9.model.TipoAlerta

/**
 * Convierte los enums del proyecto a String y viceversa para que Room los
 * pueda guardar en SQLite (SQLite no tiene un tipo "enum" nativo).
 * Se guarda el nombre (ej. "LIBRE") en vez del ordinal (0,1,2) para que la
 * base de datos siga siendo legible si se agrega un valor nuevo al enum.
 */
class Converters {

    @TypeConverter
    fun fromEstadoProbador(valor: EstadoProbador): String = valor.name

    @TypeConverter
    fun toEstadoProbador(valor: String): EstadoProbador = EstadoProbador.valueOf(valor)

    @TypeConverter
    fun fromTipoAlerta(valor: TipoAlerta): String = valor.name

    @TypeConverter
    fun toTipoAlerta(valor: String): TipoAlerta = TipoAlerta.valueOf(valor)

    @TypeConverter
    fun fromRol(valor: Rol): String = valor.name

    @TypeConverter
    fun toRol(valor: String): Rol = Rol.valueOf(valor)
}
