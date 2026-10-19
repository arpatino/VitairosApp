package cl.vitairos.industrial.data.remote

import cl.vitairos.industrial.data.remote.dto.ReportUploadRequest
import cl.vitairos.industrial.data.remote.dto.ReportUploadResponse

/** API simulada en proceso (latencia, errores y estados). Sin red ni credenciales reales. */
class MockReportApi : ReportRemoteDataSource {
    override suspend fun upload(request: ReportUploadRequest): ReportUploadResponse {
        TODO("Simular latencia, exito, error y estado de procesamiento")
    }
}
