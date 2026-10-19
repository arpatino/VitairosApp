package cl.vitairos.industrial.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sync_attempts",
    foreignKeys = [ForeignKey(
        entity = ReportEntity::class,
        parentColumns = ["id"],
        childColumns = ["reportId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("reportId")],
)
data class SyncAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportId: String,
    val attemptNumber: Int,
    val startedAtMillis: Long,
    val finishedAtMillis: Long?,
    val success: Boolean?,
    val message: String?,
)
