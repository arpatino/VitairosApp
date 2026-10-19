package cl.vitairos.industrial.di

import android.content.Context
import androidx.room.Room
import cl.vitairos.industrial.data.local.db.VitairosDatabase
import cl.vitairos.industrial.data.local.files.PrivateFileStore
import cl.vitairos.industrial.data.local.prefs.SessionDataStore
import cl.vitairos.industrial.data.remote.MockReportApi
import cl.vitairos.industrial.data.remote.ReportRemoteDataSource
import cl.vitairos.industrial.data.repository.AuthRepositoryImpl
import cl.vitairos.industrial.data.repository.CatalogRepositoryImpl
import cl.vitairos.industrial.data.repository.ReportRepositoryImpl
import cl.vitairos.industrial.data.repository.SyncRepositoryImpl
import cl.vitairos.industrial.data.sync.ConnectivityObserver
import cl.vitairos.industrial.data.sync.SyncScheduler
import cl.vitairos.industrial.domain.repository.AuthRepository
import cl.vitairos.industrial.domain.repository.CatalogRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.domain.repository.SyncRepository

/** Inyección de dependencias manual (simple y explicable para el MVP). */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database: VitairosDatabase by lazy {
        Room.databaseBuilder(appContext, VitairosDatabase::class.java, VitairosDatabase.NAME).build()
    }
    private val sessionStore by lazy { SessionDataStore(appContext) }
    private val remote: ReportRemoteDataSource by lazy { MockReportApi() }

    val fileStore by lazy { PrivateFileStore(appContext) }
    val connectivity by lazy { ConnectivityObserver(appContext) }
    val syncScheduler by lazy { SyncScheduler(appContext) }

    val reportRepository: ReportRepository by lazy { ReportRepositoryImpl(database.reportDao()) }
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(sessionStore) }
    val catalogRepository: CatalogRepository by lazy { CatalogRepositoryImpl() }
    val syncRepository: SyncRepository by lazy { SyncRepositoryImpl(database.reportDao(), remote, syncScheduler) }
    val saveDraftUseCase by lazy { cl.vitairos.industrial.domain.usecase.SaveDraftUseCase(reportRepository) }
    val deleteReportUseCase by lazy { cl.vitairos.industrial.domain.usecase.DeleteReportUseCase(reportRepository) }
}
