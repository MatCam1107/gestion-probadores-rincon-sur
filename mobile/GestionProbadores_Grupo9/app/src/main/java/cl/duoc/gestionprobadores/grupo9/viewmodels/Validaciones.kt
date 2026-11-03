package cl.duoc.gestionprobadores.grupo9.viewmodels

object Validaciones {
    private val correo = Regex("^[A-Za-z0-9._%+-]+@rinconsur\\.cl$")
    private val nombre = Regex("^[A-Za-zÁÉÍÓÚáéíóúÜüÑñ]+(?: [A-Za-zÁÉÍÓÚáéíóúÜüÑñ]+)+$")

    fun errorCorreo(valor: String): String? = when {
        valor.isBlank() -> "Ingresa el correo corporativo"
        !correo.matches(valor.trim()) -> "Usa un correo @rinconsur.cl"
        else -> null
    }

    fun errorContrasena(valor: String, obligatoria: Boolean = true): String? = when {
        obligatoria && valor.isBlank() -> "Ingresa la contraseña"
        valor.isNotBlank() && valor.length < 8 -> "Debe tener al menos 8 caracteres"
        else -> null
    }

    fun errorNombre(valor: String): String? = when {
        valor.isBlank() -> "Ingresa el nombre completo"
        !nombre.matches(valor.trim()) -> "Escribe nombre y apellido usando solo letras"
        else -> null
    }

    fun errorDescripcion(valor: String): String? = when {
        valor.isBlank() -> "Describe la incidencia"
        valor.trim().length < 5 -> "Escribe al menos 5 caracteres"
        else -> null
    }
}
