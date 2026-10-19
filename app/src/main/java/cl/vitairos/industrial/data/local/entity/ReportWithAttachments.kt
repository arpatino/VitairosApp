package cl.vitairos.industrial.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ReportWithAttachments(
    @Embedded val report: ReportEntity,
    @Relation(parentColumn = "id", entityColumn = "reportId")
    val attachments: List<AttachmentEntity>,
)
