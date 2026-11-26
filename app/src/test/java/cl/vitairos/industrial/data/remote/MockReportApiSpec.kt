package cl.vitairos.industrial.data.remote

import cl.vitairos.industrial.data.remote.dto.ReportUploadRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// Pruebas unitarias con Kotest para la API Mock y simulacion de fallos
class MockReportApiSpec : StringSpec({

    "en modo simulacion local upload retorna respuesta inmediata con simulated=true" {
        MockReportApi.simulateFailure = false
        MockReportApi.useVercelCloud = false

        val api = MockReportApi()
        val request = ReportUploadRequest(
            reportId = "REP-LOCAL-01",
            typeId = "shift",
            processId = "extraction",
            userId = "u-operador",
            createdAtMillis = 1000L,
            status = "QUEUED",
            version = 1,
            attachmentRefs = emptyList(),
        )

        val response = api.upload(request)
        response.receivedId shouldBe "REP-LOCAL-01"
        response.status shouldBe "SYNCED"
        response.simulated shouldBe true
    }

    "cuando simulateFailure esta activado lanza IllegalStateException" {
        MockReportApi.simulateFailure = true

        val api = MockReportApi()
        val request = ReportUploadRequest(
            reportId = "REP-FAIL-01",
            typeId = "shift",
            processId = "extraction",
            userId = "u-operador",
            createdAtMillis = 1000L,
            status = "QUEUED",
            version = 1,
            attachmentRefs = emptyList(),
        )

        val exception = shouldThrow<IllegalStateException> {
            api.upload(request)
        }
        exception.message shouldBe "Fallo simulado de conexión con servidor VITAIROS (Backoff activado)"

        // Restauramos el estado
        MockReportApi.simulateFailure = false
    }
})
