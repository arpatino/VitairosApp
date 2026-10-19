package cl.vitairos.industrial.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attachments",
    foreignKeys = [ForeignKey(
        entity = ReportEntity::class,
        parentColumns = ["id"],
        childColumns = ["reportId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("reportId")],
)
data class AttachmentEntity(
    @PrimaryKey val id: String,
    val reportId: String,
    val kind: String,
    val mimeType: String,
    val fileName: String,
    val relativePath: String,
    val sizeBytes: Long,
    val sha256: String?,
)
