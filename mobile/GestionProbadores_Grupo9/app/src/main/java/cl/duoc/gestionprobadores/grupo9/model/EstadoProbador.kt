package cl.duoc.gestionprobadores.grupo9.model

/**
 * Estado de ocupación de un probador.
 *
 * - [LIBRE]: nadie lo está usando.
 * - [OCUPADO]: un cliente lo está usando, dentro del tiempo límite.
 * - [EXCEDIDO]: un cliente lo está usando y superó el umbral de minutos
 *   configurado en ConfigUiState (pantalla de Configuración).
 *
 * Nota: "incidencia" (prenda olvidada) NO es un estado del probador, es una
 * Alerta de tipo TipoAlerta.PRENDA_OLVIDADA asociada al probador. Así, un
 * probador puede quedar EXCEDIDO y además tener una alerta de prenda olvidada
 * al mismo tiempo, sin mezclar ambos conceptos en un solo campo.
 */
enum class EstadoProbador {
    LIBRE,
    OCUPADO,
    EXCEDIDO
}
