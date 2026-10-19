package cl.vitairos.industrial.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import cl.vitairos.industrial.data.local.dao.ReportDao
import cl.vitairos.industrial.data.local.entity.AttachmentEntity
import cl.vitairos.industrial.data.local.entity.ReportEntity
import cl.vitairos.industrial.data.local.entity.SyncAttemptEntity

@Database(
    entities = [ReportEntity::class, AttachmentEntity::class, SyncAttemptEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class VitairosDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao

    companion object {
        const val NAME = "vitairos.db"
    }
}
