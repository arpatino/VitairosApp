package cl.vitairos.industrial.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cl.vitairos.industrial.data.local.entity.AttachmentEntity
import cl.vitairos.industrial.data.local.entity.ReportEntity
import cl.vitairos.industrial.data.local.entity.ReportWithAttachments
import cl.vitairos.industrial.data.local.entity.SyncAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Transaction
    @Query("SELECT * FROM reports ORDER BY createdAtMillis DESC")
    fun observeAll(): Flow<List<ReportWithAttachments>>

    @Transaction
    @Query("SELECT * FROM reports WHERE status IN ('QUEUED','SENDING','ERROR','NEEDS_CORRECTION') ORDER BY createdAtMillis ASC")
    fun observeQueue(): Flow<List<ReportWithAttachments>>

    @Transaction
    @Query("SELECT * FROM reports WHERE id = :id")
    fun observe(id: String): Flow<ReportWithAttachments?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReport(report: ReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttachments(attachments: List<AttachmentEntity>)

    @Query("DELETE FROM attachments WHERE reportId = :reportId")
    suspend fun deleteAttachments(reportId: String)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM sync_attempts WHERE reportId = :reportId ORDER BY attemptNumber DESC")
    fun observeAttempts(reportId: String): Flow<List<SyncAttemptEntity>>

    @Insert
    suspend fun insertAttempt(attempt: SyncAttemptEntity): Long
}
