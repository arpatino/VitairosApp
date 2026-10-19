package cl.vitairos.industrial.domain.repository

import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.SyncAttempt
import kotlinx.coroutines.flow.Flow

/** Fuente de verdad local (Room) de los reportes. */
interface ReportRepository {
    fun observeAll(): Flow<List<Report>>
    fun observeQueue(): Flow<List<Report>>
    fun observe(reportId: String): Flow<Report?>
    fun observeAttempts(reportId: String): Flow<List<SyncAttempt>>
    suspend fun save(report: Report)
    suspend fun delete(reportId: String)
}
