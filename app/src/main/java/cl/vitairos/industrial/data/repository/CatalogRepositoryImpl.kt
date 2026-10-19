package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.domain.model.Process
import cl.vitairos.industrial.domain.model.ReportType
import cl.vitairos.industrial.domain.repository.CatalogRepository

/** Catalogo cerrado y acotado (datos semilla locales, a validar con VITAIROS). */
class CatalogRepositoryImpl : CatalogRepository {
    override suspend fun reportTypes() = listOf(
        ReportType("shift", "Reporte de turno", "1.0"),
        ReportType("maintenance", "Reporte de mantención", "1.0"),
        ReportType("incident", "Reporte de incidente", "1.0"),
    )

    override suspend fun processes() = listOf(
        Process("extraction", "Extracción"),
        Process("processing", "Procesamiento"),
        Process("maintenance", "Mantención"),
        Process("safety", "Seguridad"),
    )
}
