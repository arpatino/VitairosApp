package cl.vitairos.industrial.domain.model

/** Proceso asociado: se elige de una lista fija, no se escribe libremente. */
data class Process(
    val id: String,
    val name: String,
)
