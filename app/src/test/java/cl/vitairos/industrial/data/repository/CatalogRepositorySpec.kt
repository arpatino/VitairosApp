package cl.vitairos.industrial.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

// Pruebas unitarias con Kotest para los catalogos cerrados de la faena
class CatalogRepositorySpec : FunSpec({

    val repository = CatalogRepositoryImpl()

    test("reportTypes entrega los 3 tipos de reporte semilla acordados") {
        val types = repository.reportTypes()
        types.size shouldBe 3
        types.map { it.id } shouldContainExactly listOf("shift", "maintenance", "incident")
        types.first().name shouldBe "Reporte de turno"
    }

    test("processes entrega los 4 procesos operacionales de la planta") {
        val processes = repository.processes()
        processes.size shouldBe 4
        processes.map { it.id } shouldContainExactly listOf("extraction", "processing", "maintenance", "safety")
        processes.map { it.name } shouldContainExactly listOf("Extracción", "Procesamiento", "Mantención", "Seguridad")
    }
})
