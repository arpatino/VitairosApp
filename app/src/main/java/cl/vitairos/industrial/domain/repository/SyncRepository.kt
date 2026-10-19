package cl.vitairos.industrial.domain.repository

/** Orquestación de la cola de envío y sus reintentos. */
interface SyncRepository {
    /** Pasa un reporte a EN_COLA y agenda la sincronización. */
    suspend fun enqueue(reportId: String)

    /** Reintento manual tras agotar los automáticos. */
    suspend fun retryNow(reportId: String)

    /** Procesa la cola pendiente (invocado por el worker). */
    suspend fun syncPending(): SyncOutcome
}

enum class SyncOutcome { SUCCESS, RETRY, FAILURE }
