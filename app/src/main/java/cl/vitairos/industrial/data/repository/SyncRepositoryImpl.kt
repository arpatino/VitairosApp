package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.data.local.dao.ReportDao
import cl.vitairos.industrial.data.remote.ReportRemoteDataSource
import cl.vitairos.industrial.data.sync.SyncScheduler
import cl.vitairos.industrial.domain.repository.SyncOutcome
import cl.vitairos.industrial.domain.repository.SyncRepository

class SyncRepositoryImpl(
    private val dao: ReportDao,
    private val remote: ReportRemoteDataSource,
    private val scheduler: SyncScheduler,
) : SyncRepository {
    override suspend fun enqueue(reportId: String) {
        TODO("Marcar EN_COLA y llamar scheduler.schedule()")
    }

    override suspend fun retryNow(reportId: String) {
        TODO("Reiniciar contador y agendar sincronizacion manual")
    }

    override suspend fun syncPending(): SyncOutcome {
        TODO("Enviar reportes EN_COLA al remote, registrar SyncAttempt y actualizar estados")
    }
}
