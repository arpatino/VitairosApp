package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.data.local.dao.ReportDao
import cl.vitairos.industrial.data.local.mapper.toDomain
import cl.vitairos.industrial.data.local.mapper.toEntity
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.SyncAttempt
import cl.vitairos.industrial.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReportRepositoryImpl(private val dao: ReportDao) : ReportRepository {
    override fun observeAll(): Flow<List<Report>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override fun observeQueue(): Flow<List<Report>> = dao.observeQueue().map { list -> list.map { it.toDomain() } }
    override fun observe(reportId: String): Flow<Report?> = dao.observe(reportId).map { it?.toDomain() }
    override fun observeAttempts(reportId: String): Flow<List<SyncAttempt>> =
        dao.observeAttempts(reportId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(report: Report) {
        dao.upsertReport(report.toEntity())
        dao.deleteAttachments(report.id)
        dao.upsertAttachments(report.attachments.map { it.toEntity() })
    }

    override suspend fun delete(reportId: String) = dao.delete(reportId)
}
