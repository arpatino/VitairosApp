package cl.vitairos.industrial.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.vitairos.industrial.VitairosApp
import cl.vitairos.industrial.domain.repository.SyncOutcome

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val container = (applicationContext as VitairosApp).container
        return when (container.syncRepository.syncPending()) {
            SyncOutcome.SUCCESS -> Result.success()
            SyncOutcome.RETRY ->
                if (runAttemptCount < RetryPolicy.MAX_AUTOMATIC_RETRIES) Result.retry() else Result.failure()
            SyncOutcome.FAILURE -> Result.failure()
        }
    }
}
