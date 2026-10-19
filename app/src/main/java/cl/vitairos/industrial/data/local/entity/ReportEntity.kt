package cl.vitairos.industrial.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val typeId: String,
    val typeVersion: String,
    val processId: String,
    val createdAtMillis: Long,
    val userId: String,
    val areaOrSite: String?,
    val shift: String?,
    val observations: String?,
    val status: String,
    val attemptCount: Int,
    val lastSyncAtMillis: Long?,
    val version: Int,
    val installationId: String?,
)
