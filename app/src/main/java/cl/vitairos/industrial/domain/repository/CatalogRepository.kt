package cl.vitairos.industrial.domain.repository

import cl.vitairos.industrial.domain.model.Process
import cl.vitairos.industrial.domain.model.ReportType

interface CatalogRepository {
    suspend fun reportTypes(): List<ReportType>
    suspend fun processes(): List<Process>
}
