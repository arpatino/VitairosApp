package cl.vitairos.industrial.domain.usecase

import cl.vitairos.industrial.createTestReport
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.domain.repository.SyncOutcome
import cl.vitairos.industrial.domain.repository.SyncRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

// Pruebas unitarias con Kotest y MockK para los casos de uso principales
class ReportUseCasesSpec : BehaviorSpec({

    Given("el caso de uso SaveDraftUseCase") {
        val reportRepo = mockk<ReportRepository>(relaxed = true)
        val saveDraft = SaveDraftUseCase(reportRepo)

        When("se guarda un reporte en borrador") {
            val report = createTestReport(
                id = "REP-DRAFT-1",
                status = ReportStatus.DRAFT,
                observations = "Nota borrador",
            )

            saveDraft(report)

            Then("delega el guardado en reportRepository.save") {
                coVerify(exactly = 1) { reportRepo.save(report) }
            }
        }
    }

    Given("el caso de uso EnqueueReportUseCase") {
        val reportRepo = mockk<ReportRepository>(relaxed = true)
        val syncRepo = mockk<SyncRepository>(relaxed = true)
        val enqueueReport = EnqueueReportUseCase(reportRepo, syncRepo)

        When("se encola un reporte para despacho") {
            enqueueReport("REP-100")

            Then("delega el encolado en syncRepository.enqueue") {
                coVerify(exactly = 1) { syncRepo.enqueue("REP-100") }
            }
        }
    }

    Given("el caso de uso DeleteReportUseCase") {
        val reportRepo = mockk<ReportRepository>(relaxed = true)
        val deleteReport = DeleteReportUseCase(reportRepo)

        When("se elimina un reporte por id") {
            deleteReport("REP-DELETE-9")

            Then("delega la eliminación en reportRepository.delete") {
                coVerify(exactly = 1) { reportRepo.delete("REP-DELETE-9") }
            }
        }
    }

    Given("el caso de uso SyncPendingReportsUseCase") {
        val syncRepo = mockk<SyncRepository>()
        coEvery { syncRepo.syncPending() } returns SyncOutcome.SUCCESS
        val syncPending = SyncPendingReportsUseCase(syncRepo)

        When("se sincronizan los reportes pendientes") {
            val outcome = syncPending()

            Then("retorna el resultado reportado por syncRepository") {
                outcome shouldBe SyncOutcome.SUCCESS
                coVerify(exactly = 1) { syncRepo.syncPending() }
            }
        }
    }
})
