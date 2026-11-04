package cl.duoc.gestionprobadores.grupo9.repository.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.gestionprobadores.grupo9.repository.models.Incidencia
import cl.duoc.gestionprobadores.grupo9.repository.models.Probador
import cl.duoc.gestionprobadores.grupo9.repository.models.UsoProbador
import cl.duoc.gestionprobadores.grupo9.repository.models.Usuario

@Database(
    entities = [Usuario::class, Probador::class, Incidencia::class, UsoProbador::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun probadorDao(): ProbadorDao
    abstract fun incidenciaDao(): IncidenciaDao
    abstract fun usoProbadorDao(): UsoProbadorDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase = instancia ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "gestion_probadores.db"
            )
                .fallbackToDestructiveMigration()
                .addCallback(datosIniciales)
                .build()
                .also { instancia = it }
        }

        private val datosIniciales = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO usuarios (nombre,email,contrasena,rol,sucursales,activo) VALUES ('Admin Rincón Sur','admin@rinconsur.cl','12345678','ADMINISTRADOR','Plaza Norte|Mall Vespucio|Mall Tobalaba|Costanera Center',1)")
                db.execSQL("INSERT INTO usuarios (nombre,email,contrasena,rol,sucursales,activo) VALUES ('Ana Rojas','ana.rojas@rinconsur.cl','12345678','COLABORADOR','Plaza Norte',1)")
                db.execSQL("INSERT INTO usuarios (nombre,email,contrasena,rol,sucursales,activo) VALUES ('Carlos Pérez','carlos.perez@rinconsur.cl','12345678','COLABORADOR','Plaza Norte|Mall Vespucio|Mall Tobalaba',1)")
                db.execSQL("INSERT INTO usuarios (nombre,email,contrasena,rol,sucursales,activo) VALUES ('María López','maria.lopez@rinconsur.cl','12345678','COLABORADOR','Mall Vespucio',1)")

                val sucursales = listOf(
                    "Plaza Norte" to 6,
                    "Mall Vespucio" to 5,
                    "Mall Tobalaba" to 4,
                    "Costanera Center" to 6
                )
                sucursales.forEach { (sucursal, cantidad) ->
                    repeat(cantidad) { indice ->
                        db.execSQL(
                            "INSERT INTO probadores (sucursal,numero,estado,horaInicio,cantidadPrendas,motivoClausura) VALUES (?,?, 'LIBRE',NULL,0,NULL)",
                            arrayOf(sucursal, indice + 1)
                        )
                    }
                }

                val ahora = System.currentTimeMillis()
                db.execSQL("UPDATE probadores SET estado='OCUPADO', horaInicio=?, cantidadPrendas=4 WHERE id=2", arrayOf(ahora - 4 * 60_000L))
                db.execSQL("UPDATE probadores SET estado='OCUPADO', horaInicio=?, cantidadPrendas=3 WHERE id=3", arrayOf(ahora - 14 * 60_000L))
                db.execSQL("UPDATE probadores SET estado='EXCEDIDO', horaInicio=?, cantidadPrendas=5 WHERE id=4", arrayOf(ahora - 18 * 60_000L))
                db.execSQL("UPDATE probadores SET estado='CLAUSURADO', motivoClausura='En limpieza' WHERE id=14")
            }
        }
    }
}
