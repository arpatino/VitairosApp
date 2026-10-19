package cl.vitairos.industrial.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReportUploadRequest(
    val reportId: String,
    val typeId: String,
    val processId: String,
    val userId: String,
    val createdAtMillis: Long,
    val status: String,
    val version: Int,
    val attachmentRefs: List<String>,
)

@Serializable
data class ReportUploadResponse(
    val receivedId: String,
    val status: String,
    val message: String,
    /** Etiquetar siempre como Simulacion en la UI. */
    val simulated: Boolean = true,
)
