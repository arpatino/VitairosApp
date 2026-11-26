package cl.vitairos.industrial

import cl.vitairos.industrial.data.local.entity.ReportEntity
import cl.vitairos.industrial.domain.model.Attachment
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus

// Helper para crear reportes de prueba con valores por defecto
fun createTestReport(
    id: String = "REP-TEST",
    typeId: String = "shift",
    typeVersion: String = "1.0",
    processId: String = "extraction",
    createdAtMillis: Long = 1000L,
    userId: String = "u-operador",
    areaOrSite: String? = "Mina",
    shift: String? = "Turno 1",
    observations: String? = "Obs",
    status: ReportStatus = ReportStatus.QUEUED,
    attachments: List<Attachment> = emptyList(),
    attemptCount: Int = 0,
    lastSyncAtMillis: Long? = null,
    version: Int = 1,
    installationId: String? = "DEV-01",
) = Report(
    id = id,
    typeId = typeId,
    typeVersion = typeVersion,
    processId = processId,
    createdAtMillis = createdAtMillis,
    userId = userId,
    areaOrSite = areaOrSite,
    shift = shift,
    observations = observations,
    status = status,
    attachments = attachments,
    attemptCount = attemptCount,
    lastSyncAtMillis = lastSyncAtMillis,
    version = version,
    installationId = installationId,
)

// Helper para crear entidades de reporte para Room con valores por defecto
fun createTestReportEntity(
    id: String = "REP-TEST",
    typeId: String = "shift",
    typeVersion: String = "1.0",
    processId: String = "extraction",
    createdAtMillis: Long = 1000L,
    userId: String = "u-operador",
    areaOrSite: String? = "Mina",
    shift: String? = "Turno 1",
    observations: String? = "Obs",
    status: String = ReportStatus.QUEUED.name,
    attemptCount: Int = 0,
    lastSyncAtMillis: Long? = null,
    version: Int = 1,
    installationId: String? = "DEV-01",
) = ReportEntity(
    id = id,
    typeId = typeId,
    typeVersion = typeVersion,
    processId = processId,
    createdAtMillis = createdAtMillis,
    userId = userId,
    areaOrSite = areaOrSite,
    shift = shift,
    observations = observations,
    status = status,
    attemptCount = attemptCount,
    lastSyncAtMillis = lastSyncAtMillis,
    version = version,
    installationId = installationId,
)
