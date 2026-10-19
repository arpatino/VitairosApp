package cl.vitairos.industrial.data.sync

/** 3 reintentos automaticos con backoff simple de 5, 15 y 30 s. */
object RetryPolicy {
    val BACKOFF_SECONDS = listOf(5L, 15L, 30L)
    val MAX_AUTOMATIC_RETRIES: Int get() = BACKOFF_SECONDS.size

    fun delaySecondsFor(retryIndex: Int): Long? = BACKOFF_SECONDS.getOrNull(retryIndex)
}
