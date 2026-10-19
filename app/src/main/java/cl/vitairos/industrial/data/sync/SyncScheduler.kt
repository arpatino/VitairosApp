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

    fun schedule() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.LINEAR, RetryPolicy.BACKOFF_SECONDS.first(), TimeUnit.SECONDS)
            .build()
        workManager.enqueueUniqueWork(UNIQUE_NAME, ExistingWorkPolicy.KEEP, request)
    }

    companion object {
        const val UNIQUE_NAME = "vitairos-sync"
    }
}
