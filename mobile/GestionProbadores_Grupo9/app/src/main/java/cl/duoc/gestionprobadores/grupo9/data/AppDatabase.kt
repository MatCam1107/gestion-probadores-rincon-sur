package cl.duoc.gestionprobadores.grupo9.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.gestionprobadores.grupo9.data.dao.AlertaDao
import cl.duoc.gestionprobadores.grupo9.data.dao.ProbadorDao
import cl.duoc.gestionprobadores.grupo9.data.dao.RegistroColaDao
import cl.duoc.gestionprobadores.grupo9.data.dao.RegistroUsoDao
import cl.duoc.gestionprobadores.grupo9.data.dao.ReportePrendaDao
import cl.duoc.gestionprobadores.grupo9.data.dao.UsuarioDao
import cl.duoc.gestionprobadores.grupo9.model.EstadoProbador
import cl.duoc.gestionprobadores.grupo9.model.Probador
import cl.duoc.gestionprobadores.grupo9.model.Rol
import cl.duoc.gestionprobadores.grupo9.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Base de datos Room del proyecto. Agrupa las 6 entidades y expone sus DAOs.
 *
 * Se usa un único singleton ([getDatabase]) para toda la app, creado la
 * primera vez que se necesita (desde el Application o desde el primer
 * Repository que lo pide) y reutilizado después.
 */
@Database(
    entities = [
        Probador::class,
        cl.duoc.gestionprobadores.grupo9.model.RegistroUso::class,
        cl.duoc.gestionprobadores.grupo9.model.Alerta::class,
        cl.duoc.gestionprobadores.grupo9.model.RegistroCola::class,
        cl.duoc.gestionprobadores.grupo9.model.ReportePrenda::class,
        Usuario::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun probadorDao(): ProbadorDao
    abstract fun registroUsoDao(): RegistroUsoDao
    abstract fun alertaDao(): AlertaDao
    abstract fun registroColaDao(): RegistroColaDao
    abstract fun reportePrendaDao(): ReportePrendaDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Credencial de demostración, igual a la del mockup: clave 12345678. */
        private const val CLAVE_DEMO = "12345678"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestion_probadores.db"
                )
                    .addCallback(SeedCallback(context.applicationContext))
                    .build()
                INSTANCE = instancia
                instancia
            }
        }

        /**
         * Precarga datos ficticios la PRIMERA vez que se crea la base de
         * datos (no se vuelve a ejecutar en los siguientes arranques):
         * 10 probadores libres y dos usuarios de demostración.
         */
        private class SeedCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                    val database = getDatabase(context)

                    val probadores = (1..10).map { numero ->
                        Probador(numero = "Probador $numero", estado = EstadoProbador.LIBRE)
                    }
                    probadores.forEach { database.probadorDao().insertar(it) }

                    database.usuarioDao().insertar(
                        Usuario(
                            nombre = "Vendedor Demo",
                            correo = "vendedor@rinconsur.cl",
                            clave = CLAVE_DEMO,
                            rol = Rol.VENDEDOR
                        )
                    )
                    database.usuarioDao().insertar(
                        Usuario(
                            nombre = "Supervisor Demo",
                            correo = "supervisor@rinconsur.cl",
                            clave = CLAVE_DEMO,
                            rol = Rol.SUPERVISOR
                        )
                    )
                }
            }
        }
    }
}
