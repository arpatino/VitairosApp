package cl.vitairos.industrial.domain.model

/** Estados de sincronización visibles al usuario (Cuestionario §6). */
enum class ReportStatus {
    DRAFT,
    QUEUED,
    SENDING,
    SYNCED,
    ERROR,
    NEEDS_CORRECTION;

    /** Editable/eliminable solo mientras no haya comenzado el envío (Cuestionario §3). */
    val isEditable: Boolean
        get() = this == DRAFT || this == QUEUED || this == ERROR || this == NEEDS_CORRECTION
}
