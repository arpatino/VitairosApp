package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.createTestReport
import cl.vitairos.industrial.createTestReportEntity
import cl.vitairos.industrial.data.local.dao.ReportDao
import cl.vitairos.industrial.data.local.entity.ReportWithAttachments
import cl.vitairos.industrial.domain.model.Attachment
import cl.vitairos.industrial.domain.model.AttachmentKind
import cl.vitairos.industrial.domain.model.ReportStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

// Pruebas unitarias con Kotest para la persistencia y lectura de reportes con ReportDao
class ReportRepositorySpec : FunSpec({

    val dao = mockk<ReportDao>(relaxed = true)
    val repository = ReportRepositoryImpl(dao)

    test("save persiste el reporte y actualiza los adjuntos en el dao") {
        val attachment = Attachment(
            id = "ATT-1",
            reportId = "REP-01",
            kind = AttachmentKind.IMAGE,
            mimeType = "image/jpeg",
            fileName = "f.jpg",
            relativePath = "f.jpg",
            sizeBytes = 100L,
            sha256 = "sha",
        )
        val report = createTestReport(
            id = "REP-01",
            attachments = listOf(attachment),
        )

        repository.save(report)

        coVerify(exactly = 1) { dao.upsertReport(any()) }
        coVerify(exactly = 1) { dao.deleteAttachments("REP-01") }
        coVerify(exactly = 1) { dao.upsertAttachments(match { it.size == 1 }) }
    }

    test("delete invoca el borrado por id en el dao") {
        repository.delete("REP-99")
        coVerify(exactly = 1) { dao.delete("REP-99") }
    }

    test("observeAll mapea los reportes recibidos desde el dao") {
        val entity = createTestReportEntity(
            id = "REP-OBS-1",
            status = ReportStatus.SYNCED.name,
        )
        val item = ReportWithAttachments(entity, emptyList())
        every { dao.observeAll() } returns flowOf(listOf(item))

        val list = repository.observeAll().first()
        list.size shouldBe 1
        list.first().id shouldBe "REP-OBS-1"
        list.first().status shouldBe ReportStatus.SYNCED
    }
})
