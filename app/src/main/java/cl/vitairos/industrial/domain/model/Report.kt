package cl.vitairos.industrial.domain.model

/** Metadata mínima del reporte (Cuestionario §5). */
data class Report(
    /** Identificador único generado al crear el reporte (UUID). Nunca se edita. */
    val id: String,
    val typeId: String,
    val typeVersion: String,
    val processId: String,
    val createdAtMillis: Long,
    val userId: String,
    val areaOrSite: String?,
    val shift: String?,
    val observations: String?,
    val status: ReportStatus,
    val attachments: List<Attachment>,
    val attemptCount: Int,
    val lastSyncAtMillis: Long?,
    /** Versión/reenvío: una corrección crea nueva versión sin borrar la anterior. */
    val version: Int,
    val installationId: String?,
)
