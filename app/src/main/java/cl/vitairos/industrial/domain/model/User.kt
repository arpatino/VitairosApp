package cl.vitairos.industrial.domain.model

/** Usuario ficticio del login simulado. Nunca contiene contraseña. */
data class User(
    val id: String,
    val displayName: String,
    val role: UserRole,
)
