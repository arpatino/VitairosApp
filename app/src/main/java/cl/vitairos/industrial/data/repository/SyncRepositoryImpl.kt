package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.data.local.dao.ReportDao
import cl.vitairos.industrial.data.local.entity.SyncAttemptEntity
import cl.vitairos.industrial.data.remote.ReportRemoteDataSource
import cl.vitairos.industrial.data.remote.dto.ReportUploadRequest
import cl.vitairos.industrial.data.sync.RetryPolicy
import cl.vitairos.industrial.data.sync.SyncScheduler
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.repository.SyncOutcome
import cl.vitairos.industrial.domain.repository.SyncRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class SyncRepositoryImpl(
    private val dao: ReportDao,
    private val remote: ReportRemoteDataSource,
    private val scheduler: SyncScheduler,
) : SyncRepository {

    override suspend fun enqueue(reportId: String) {
        val current = dao.observe(reportId).first()
        if (current != null) {
            val updated = current.report.copy(status = ReportStatus.QUEUED.name)
            dao.upsertReport(updated)
        }
        scheduler.schedule()
    }

    override suspend fun retryNow(reportId: String) {
        val current = dao.observe(reportId).first()
        if (current != null) {
            // Reiniciamos el contador de intentos a 0 y lo volvemos a poner EN_COLA
            val updated = current.report.copy(
                status = ReportStatus.QUEUED.name,
                attemptCount = 0
            )
            dao.upsertReport(updated)
        }
        scheduler.schedule()
    }

    override suspend fun syncPending(): SyncOutcome {
        val queueList = dao.observeQueue().first()
        // Filtramos los que estan en cola o quedaron enviando
        val pendingReports = queueList.filter {
            it.report.status == ReportStatus.QUEUED.name || it.report.status == ReportStatus.SENDING.name
        }

        if (pendingReports.isEmpty()) {
            return SyncOutcome.SUCCESS
        }

        var allSucceeded = true

        for (item in pendingReports) {
            var reportEntity = item.report
            val attachments = item.attachments
            var synced = false

            // Ciclo de envio y reintentos automaticos con backoff (5s, 15s, 30s)
            while (!synced) {
                val startedAt = System.currentTimeMillis()
                val currentAttemptNumber = reportEntity.attemptCount + 1

                // Cambiamos el estado a SENDING (Enviando) para que se vea en la UI
                reportEntity = reportEntity.copy(status = ReportStatus.SENDING.name)
                dao.upsertReport(reportEntity)

                try {
                    val request = ReportUploadRequest(
                        reportId = reportEntity.id,
                        typeId = reportEntity.typeId,
                        processId = reportEntity.processId,
                        userId = reportEntity.userId,
                        createdAtMillis = reportEntity.createdAtMillis,
                        status = reportEntity.status,
                        version = reportEntity.version,
                        attachmentRefs = attachments.map { it.fileName }
                    )

                    val response = remote.upload(request)
                    val finishedAt = System.currentTimeMillis()

                    // Registramos el intento exitoso en la tabla sync_attempts
                    dao.insertAttempt(
                        SyncAttemptEntity(
                            reportId = reportEntity.id,
                            attemptNumber = currentAttemptNumber,
                            startedAtMillis = startedAt,
                            finishedAtMillis = finishedAt,
                            success = true,
                            message = response.message
                        )
                    )

                    // Marcamos el reporte como SYNCED (Sincronizado)
                    reportEntity = reportEntity.copy(
                        status = ReportStatus.SYNCED.name,
                        attemptCount = currentAttemptNumber,
                        lastSyncAtMillis = finishedAt
                    )
                    dao.upsertReport(reportEntity)
                    synced = true
                } catch (e: Exception) {
                    val finishedAt = System.currentTimeMillis()
                    val delaySeconds = RetryPolicy.delaySecondsFor(reportEntity.attemptCount)

                    // Registramos el intento fallido en la tabla sync_attempts
                    val attemptMessage = if (delaySeconds != null) {
                        "Fallo intento $currentAttemptNumber. Reintentando en ${delaySeconds}s..."
                    } else {
                        "Se agotaron los ${RetryPolicy.MAX_AUTOMATIC_RETRIES} reintentos automáticos. Requiere reintento manual."
                    }

                    dao.insertAttempt(
                        SyncAttemptEntity(
                            reportId = reportEntity.id,
                            attemptNumber = currentAttemptNumber,
                            startedAtMillis = startedAt,
                            finishedAtMillis = finishedAt,
                            success = false,
                            message = attemptMessage
                        )
                    )

                    if (delaySeconds != null) {
                        // Aun quedan reintentos automaticos: aplicamos el backoff (5s, 15s o 30s)
                        reportEntity = reportEntity.copy(
                            status = ReportStatus.QUEUED.name,
                            attemptCount = currentAttemptNumber
                        )
                        dao.upsertReport(reportEntity)
                        delay(delaySeconds * 1000L)
                    } else {
                        // Ya se hicieron los 3 reintentos (5s, 15s, 30s): pasa a estado ERROR
                        reportEntity = reportEntity.copy(
                            status = ReportStatus.ERROR.name,
                            attemptCount = currentAttemptNumber
                        )
                        dao.upsertReport(reportEntity)
                        allSucceeded = false
                        break
                    }
                }
            }
        }

        return if (allSucceeded) SyncOutcome.SUCCESS else SyncOutcome.FAILURE
    }
}
