package cl.vitairos.industrial.domain.model

/** Intento de sincronización: base de la trazabilidad. */
data class SyncAttempt(
    val id: Long,
    val reportId: String,
    val attemptNumber: Int,
    val startedAtMillis: Long,
    val finishedAtMillis: Long?,
    val success: Boolean?,
    val message: String?,
)
