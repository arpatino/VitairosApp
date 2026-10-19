package cl.vitairos.industrial.domain.usecase

import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.domain.repository.SyncRepository

// Casos de uso del flujo principal. Solo firmas: la lógica se implementa en fases siguientes.

class SaveDraftUseCase(private val reports: ReportRepository) {
    suspend operator fun invoke(report: Report) {
        reports.save(report)
    }
}

class EnqueueReportUseCase(
    private val reports: ReportRepository,
    private val sync: SyncRepository,
) {
    suspend operator fun invoke(reportId: String) {
        sync.enqueue(reportId)
    }
}

class DeleteReportUseCase(private val reports: ReportRepository) {
    suspend operator fun invoke(reportId: String) {
        reports.delete(reportId)
    }
}

class SyncPendingReportsUseCase(private val sync: SyncRepository) {
    suspend operator fun invoke() = sync.syncPending()
}
