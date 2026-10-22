package cl.vitairos.industrial.data.remote

import cl.vitairos.industrial.data.remote.dto.ReportUploadRequest
import cl.vitairos.industrial.data.remote.dto.ReportUploadResponse
import kotlinx.coroutines.delay

/** API simulada en proceso (latencia, errores y estados). Sin red ni credenciales reales. */
class MockReportApi : ReportRemoteDataSource {

    companion object {
        // Permite activar o desactivar la simulacion de fallo para demostrar los reintentos (5s, 15s, 30s)
        var simulateFailure: Boolean = false
    }

    override suspend fun upload(request: ReportUploadRequest): ReportUploadResponse {
        // Simulamos una latencia de red de 1.5 segundos para poder observar el estado ENVIANDO
        delay(1500)

        if (simulateFailure) {
            throw IllegalStateException("Fallo simulado de conexión con servidor VITAIROS (Mock)")
        }

        return ReportUploadResponse(
            receivedId = request.reportId,
            status = "SYNCED",
            message = "Reporte recibido y validado en servidor mock VITAIROS",
            simulated = true,
        )
    }
}
