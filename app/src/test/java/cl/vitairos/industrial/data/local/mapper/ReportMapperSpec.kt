package cl.vitairos.industrial.data.local.mapper

import cl.vitairos.industrial.data.local.entity.AttachmentEntity
import cl.vitairos.industrial.data.local.entity.ReportEntity
import cl.vitairos.industrial.data.local.entity.ReportWithAttachments
import cl.vitairos.industrial.data.local.entity.SyncAttemptEntity
import cl.vitairos.industrial.domain.model.Attachment
import cl.vitairos.industrial.domain.model.AttachmentKind
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// Pruebas unitarias con Kotest para verificar la transformacion entre Room y Dominio
class ReportMapperSpec : FunSpec({

    test("ReportWithAttachments se transforma correctamente al modelo de dominio Report") {
        val reportEntity = ReportEntity(
            id = "REP-2026-TEST",
            typeId = "shift",
            typeVersion = "1.0",
            processId = "extraction",
            createdAtMillis = 1700000000000L,
            userId = "u-operador",
            areaOrSite = "Sector Mina Norte",
            shift = "Turno 1",
            observations = "Operación normal en planta chancado",
            status = ReportStatus.QUEUED.name,
            attemptCount = 1,
            lastSyncAtMillis = null,
            version = 1,
            installationId = "DEV-VITAIROS-01",
        )

        val attachmentEntity = AttachmentEntity(
            id = "ATT-1234",
            reportId = "REP-2026-TEST",
            kind = AttachmentKind.IMAGE.name,
            mimeType = "image/jpeg",
            fileName = "foto.jpg",
            relativePath = "foto.jpg",
            sizeBytes = 2048L,
            sha256 = "abc123hash",
        )

        val reportWithAttachments = ReportWithAttachments(
            report = reportEntity,
            attachments = listOf(attachmentEntity),
        )

        val domain = reportWithAttachments.toDomain()

        domain.id shouldBe "REP-2026-TEST"
        domain.typeId shouldBe "shift"
        domain.processId shouldBe "extraction"
        domain.userId shouldBe "u-operador"
        domain.areaOrSite shouldBe "Sector Mina Norte"
        domain.shift shouldBe "Turno 1"
        domain.status shouldBe ReportStatus.QUEUED
        domain.attemptCount shouldBe 1
        domain.attachments.size shouldBe 1
        domain.attachments.first().id shouldBe "ATT-1234"
        domain.attachments.first().kind shouldBe AttachmentKind.IMAGE
    }

    test("Report de dominio se transforma correctamente a ReportEntity para Room") {
        val domain = Report(
            id = "REP-001",
            typeId = "maintenance",
            typeVersion = "1.0",
            processId = "safety",
            createdAtMillis = 1700000000L,
            userId = "u-supervisor",
            areaOrSite = "Taller Central",
            shift = "Turno 2",
            observations = "Inspección de seguridad completada",
            status = ReportStatus.SYNCED,
            attachments = emptyList(),
            attemptCount = 2,
            lastSyncAtMillis = 1700005000L,
            version = 1,
            installationId = "DEV-01",
        )

        val entity = domain.toEntity()

        entity.id shouldBe "REP-001"
        entity.typeId shouldBe "maintenance"
        entity.status shouldBe "SYNCED"
        entity.attemptCount shouldBe 2
        entity.observations shouldBe "Inspección de seguridad completada"
        entity.lastSyncAtMillis shouldBe 1700005000L
    }

    test("Attachment se mapea bidireccionalmente con AttachmentEntity") {
        val domainAttachment = Attachment(
            id = "ATT-999",
            reportId = "REP-001",
            kind = AttachmentKind.PDF,
            mimeType = "application/pdf",
            fileName = "informe.pdf",
            relativePath = "docs/informe.pdf",
            sizeBytes = 4096L,
            sha256 = "hash999",
        )

        val entity = domainAttachment.toEntity()
        entity.id shouldBe "ATT-999"
        entity.kind shouldBe "PDF"
        entity.sizeBytes shouldBe 4096L

        val mappedBack = entity.toDomain()
        mappedBack shouldBe domainAttachment
    }

    test("SyncAttemptEntity se transforma al modelo SyncAttempt de dominio") {
        val attemptEntity = SyncAttemptEntity(
            id = 5L,
            reportId = "REP-001",
            attemptNumber = 2,
            startedAtMillis = 1000L,
            finishedAtMillis = 1500L,
            success = true,
            message = "Sincronizado con éxito",
        )

        val domain = attemptEntity.toDomain()
        domain.id shouldBe 5L
        domain.reportId shouldBe "REP-001"
        domain.attemptNumber shouldBe 2
        domain.success shouldBe true
        domain.message shouldBe "Sincronizado con éxito"
    }
})
