package cl.vitairos.industrial.domain.model

enum class AttachmentKind { IMAGE, PDF }

data class Attachment(
    val id: String,
    val reportId: String,
    val kind: AttachmentKind,
    val mimeType: String,
    val fileName: String,
    /** Ruta relativa dentro del almacenamiento privado de la app. */
    val relativePath: String,
    val sizeBytes: Long,
    /** Hash del archivo para evitar reenvíos accidentales (Cuestionario §6). */
    val sha256: String?,
)
