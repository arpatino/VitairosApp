package cl.vitairos.industrial.domain.model

/** Catálogo cerrado de tipos de reporte (datos semilla locales). */
data class ReportType(
    val id: String,
    val name: String,
    val version: String,
)
