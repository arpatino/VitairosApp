package cl.vitairos.industrial.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/** Agenda la sincronizacion: solo con red; persiste tras cerrar la app o reiniciar. */
class SyncScheduler(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun schedule(initialDelaySeconds: Long = 0L) {
        val builder = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, RetryPolicy.BACKOFF_SECONDS.first(), TimeUnit.SECONDS)

        if (initialDelaySeconds > 0L) {
            builder.setInitialDelay(initialDelaySeconds, TimeUnit.SECONDS)
        }

        val request = builder.build()
        workManager.enqueueUniqueWork(UNIQUE_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    companion object {
        const val UNIQUE_NAME = "vitairos-sync"
    }
}
