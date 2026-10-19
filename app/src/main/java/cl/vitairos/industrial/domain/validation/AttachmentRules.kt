package cl.vitairos.industrial.domain.validation

/** Reglas de adjuntos acordadas con VITAIROS (Cuestionario §4). */
object AttachmentRules {
    const val MAX_ATTACHMENTS_PER_REPORT = 5
    const val MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024
    val ALLOWED_MIME_TYPES = setOf("image/jpeg", "image/png", "application/pdf")

    sealed interface Violation {
        data object EmptyFile : Violation
        data object UnsupportedFormat : Violation
        data object FileTooLarge : Violation
        data object TooManyAttachments : Violation
    }

    fun validate(mimeType: String?, sizeBytes: Long, currentCount: Int): Violation? = when {
        sizeBytes <= 0L -> Violation.EmptyFile
        mimeType == null || mimeType !in ALLOWED_MIME_TYPES -> Violation.UnsupportedFormat
        sizeBytes > MAX_FILE_SIZE_BYTES -> Violation.FileTooLarge
        currentCount >= MAX_ATTACHMENTS_PER_REPORT -> Violation.TooManyAttachments
        else -> null
    }
}
