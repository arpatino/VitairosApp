package cl.vitairos.industrial.domain.validation

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

// Pruebas unitarias con Kotest para validar las reglas de adjuntos en terreno
class AttachmentRulesSpec : StringSpec({

    "acepta archivos válidos dentro de los límites acordados" {
        // PNG de 1 KB con 0 adjuntos previos
        val violationPng = AttachmentRules.validate("image/png", 1_000L, currentCount = 0)
        violationPng shouldBe null

        // JPEG de 5 MB con 3 adjuntos previos
        val violationJpg = AttachmentRules.validate("image/jpeg", 5L * 1024 * 1024, currentCount = 3)
        violationJpg shouldBe null

        // PDF de 2 MB con 4 adjuntos previos (total 5)
        val violationPdf = AttachmentRules.validate("application/pdf", 2L * 1024 * 1024, currentCount = 4)
        violationPdf shouldBe null
    }

    "rechaza archivos vacíos con tamaño menor o igual a cero" {
        val violation = AttachmentRules.validate("image/jpeg", 0L, currentCount = 0)
        violation.shouldBeInstanceOf<AttachmentRules.Violation.EmptyFile>()

        val violationNegative = AttachmentRules.validate("application/pdf", -10L, currentCount = 0)
        violationNegative.shouldBeInstanceOf<AttachmentRules.Violation.EmptyFile>()
    }

    "rechaza formatos no soportados por la faena" {
        val violationGif = AttachmentRules.validate("image/gif", 1_000L, currentCount = 0)
        violationGif.shouldBeInstanceOf<AttachmentRules.Violation.UnsupportedFormat>()

        val violationTxt = AttachmentRules.validate("text/plain", 1_000L, currentCount = 0)
        violationTxt.shouldBeInstanceOf<AttachmentRules.Violation.UnsupportedFormat>()

        val violationNull = AttachmentRules.validate(null, 1_000L, currentCount = 0)
        violationNull.shouldBeInstanceOf<AttachmentRules.Violation.UnsupportedFormat>()
    }

    "rechaza archivos que exceden los 10 MB máximos" {
        val violation = AttachmentRules.validate("image/jpeg", 11L * 1024 * 1024, currentCount = 0)
        violation.shouldBeInstanceOf<AttachmentRules.Violation.FileTooLarge>()
    }

    "rechaza agregar más de 5 adjuntos por reporte" {
        val violation = AttachmentRules.validate("image/jpeg", 1_000L, currentCount = 5)
        violation.shouldBeInstanceOf<AttachmentRules.Violation.TooManyAttachments>()
    }
})
