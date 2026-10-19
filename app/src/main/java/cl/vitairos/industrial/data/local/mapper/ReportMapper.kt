package cl.vitairos.industrial.data.local.mapper

import cl.vitairos.industrial.data.local.entity.AttachmentEntity
import cl.vitairos.industrial.data.local.entity.ReportEntity
import cl.vitairos.industrial.data.local.entity.ReportWithAttachments
import cl.vitairos.industrial.data.local.entity.SyncAttemptEntity
import cl.vitairos.industrial.domain.model.Attachment
import cl.vitairos.industrial.domain.model.AttachmentKind
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.model.SyncAttempt

fun ReportWithAttachments.toDomain() = Report(
    id = report.id,
    typeId = report.typeId,
    typeVersion = report.typeVersion,
    processId = report.processId,
    createdAtMillis = report.createdAtMillis,
    userId = report.userId,
    areaOrSite = report.areaOrSite,
    shift = report.shift,
    observations = report.observations,
    status = ReportStatus.valueOf(report.status),
    attachments = attachments.map { it.toDomain() },
    attemptCount = report.attemptCount,
    lastSyncAtMillis = report.lastSyncAtMillis,
    version = report.version,
    installationId = report.installationId,
)

fun Report.toEntity() = ReportEntity(
    id = id,
    typeId = typeId,
    typeVersion = typeVersion,
    processId = processId,
    createdAtMillis = createdAtMillis,
    userId = userId,
    areaOrSite = areaOrSite,
    shift = shift,
    observations = observations,
    status = status.name,
    attemptCount = attemptCount,
    lastSyncAtMillis = lastSyncAtMillis,
    version = version,
    installationId = installationId,
)

fun AttachmentEntity.toDomain() = Attachment(
    id = id,
    reportId = reportId,
    kind = AttachmentKind.valueOf(kind),
    mimeType = mimeType,
    fileName = fileName,
    relativePath = relativePath,
    sizeBytes = sizeBytes,
    sha256 = sha256,
)

fun Attachment.toEntity() = AttachmentEntity(
    id = id,
    reportId = reportId,
    kind = kind.name,
    mimeType = mimeType,
    fileName = fileName,
    relativePath = relativePath,
    sizeBytes = sizeBytes,
    sha256 = sha256,
)

fun SyncAttemptEntity.toDomain() = SyncAttempt(
    id = id,
    reportId = reportId,
    attemptNumber = attemptNumber,
    startedAtMillis = startedAtMillis,
    finishedAtMillis = finishedAtMillis,
    success = success,
    message = message,
)
