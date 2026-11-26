package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.createTestReportEntity
import cl.vitairos.industrial.data.local.dao.ReportDao
import cl.vitairos.industrial.data.local.entity.ReportWithAttachments
import cl.vitairos.industrial.data.remote.ReportRemoteDataSource
import cl.vitairos.industrial.data.remote.dto.ReportUploadResponse
import cl.vitairos.industrial.data.sync.SyncScheduler
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.repository.SyncOutcome
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf

// Pruebas unitarias con Kotest para la sincronizacion de reportes y politica de reintentos
class SyncRepositorySpec : FunSpec({

    test("enqueue cambia estado a QUEUED y agenda la tarea con WorkManager") {
        val dao = mockk<ReportDao>(relaxed = true)
        val remote = mockk<ReportRemoteDataSource>(relaxed = true)
        val scheduler = mockk<SyncScheduler>(relaxed = true)
        val repo = SyncRepositoryImpl(dao, remote, scheduler)

        val reportEntity = createTestReportEntity(
            id = "REP-ENQUEUE",
            status = ReportStatus.DRAFT.name,
        )
        every { dao.observe("REP-ENQUEUE") } returns flowOf(ReportWithAttachments(reportEntity, emptyList()))

        repo.enqueue("REP-ENQUEUE")

        coVerify(exactly = 1) {
            dao.upsertReport(match { it.id == "REP-ENQUEUE" && it.status == ReportStatus.QUEUED.name })
        }
        verify(exactly = 1) { scheduler.schedule() }
    }

    test("retryNow reinicia attemptCount a 0 y vuelve a poner el estado en QUEUED") {
        val dao = mockk<ReportDao>(relaxed = true)
        val remote = mockk<ReportRemoteDataSource>(relaxed = true)
        val scheduler = mockk<SyncScheduler>(relaxed = true)
        val repo = SyncRepositoryImpl(dao, remote, scheduler)

        val reportEntity = createTestReportEntity(
            id = "REP-RETRY",
            status = ReportStatus.ERROR.name,
            attemptCount = 3,
        )
        every { dao.observe("REP-RETRY") } returns flowOf(ReportWithAttachments(reportEntity, emptyList()))

        repo.retryNow("REP-RETRY")

        coVerify(exactly = 1) {
            dao.upsertReport(match { it.id == "REP-RETRY" && it.status == ReportStatus.QUEUED.name && it.attemptCount == 0 })
        }
        verify(exactly = 1) { scheduler.schedule() }
    }

    test("syncPending sin reportes pendientes retorna SUCCESS inmediatamente") {
        val dao = mockk<ReportDao>(relaxed = true)
        val remote = mockk<ReportRemoteDataSource>(relaxed = true)
        val scheduler = mockk<SyncScheduler>(relaxed = true)
        val repo = SyncRepositoryImpl(dao, remote, scheduler)

        every { dao.observeQueue() } returns flowOf(emptyList())

        val outcome = repo.syncPending()
        outcome shouldBe SyncOutcome.SUCCESS
    }

    test("syncPending con envio exitoso actualiza a SYNCED y guarda intento exitoso") {
        val dao = mockk<ReportDao>(relaxed = true)
        val remote = mockk<ReportRemoteDataSource>(relaxed = true)
        val scheduler = mockk<SyncScheduler>(relaxed = true)
        val repo = SyncRepositoryImpl(dao, remote, scheduler)

        val reportEntity = createTestReportEntity(
            id = "REP-SYNC-OK",
            status = ReportStatus.QUEUED.name,
            attemptCount = 0,
        )
        every { dao.observeQueue() } returns flowOf(listOf(ReportWithAttachments(reportEntity, emptyList())))
        coEvery { remote.upload(any()) } returns ReportUploadResponse(
            receivedId = "REP-SYNC-OK",
            status = "SYNCED",
            message = "Recibido en servidor",
        )

        val outcome = repo.syncPending()
        outcome shouldBe SyncOutcome.SUCCESS

        // Verifica que se registro el intento exitoso
        coVerify(exactly = 1) {
            dao.insertAttempt(match { it.reportId == "REP-SYNC-OK" && it.success == true && it.attemptNumber == 1 })
        }
        // Verifica que paso a estado SYNCED
        coVerify {
            dao.upsertReport(match { it.id == "REP-SYNC-OK" && it.status == ReportStatus.SYNCED.name })
        }
    }
})
