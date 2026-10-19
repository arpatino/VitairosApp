package cl.vitairos.industrial.data.remote

import cl.vitairos.industrial.data.remote.dto.ReportUploadRequest
import cl.vitairos.industrial.data.remote.dto.ReportUploadResponse

/** Contrato de la API. Hoy lo implementa MockReportApi; luego puede ser Retrofit multipart. */
interface ReportRemoteDataSource {
    suspend fun upload(request: ReportUploadRequest): ReportUploadResponse
}
