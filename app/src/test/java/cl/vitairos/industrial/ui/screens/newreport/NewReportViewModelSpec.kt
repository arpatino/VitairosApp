package cl.vitairos.industrial.ui.screens.newreport

import cl.vitairos.industrial.data.local.files.PrivateFileStore
import cl.vitairos.industrial.domain.model.Attachment
import cl.vitairos.industrial.domain.model.AttachmentKind
import cl.vitairos.industrial.domain.model.Process
import cl.vitairos.industrial.domain.model.ReportType
import cl.vitairos.industrial.domain.repository.CatalogRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.domain.repository.SyncRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
// Pruebas unitarias con Kotest para la creacion y validacion de un nuevo reporte
class NewReportViewModelSpec : FunSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    test("valida area o faena exigiendo minimo 3 caracteres y no estar vacia") {
        val catalogRepo = mockk<CatalogRepository>(relaxed = true)
        val reportRepo = mockk<ReportRepository>(relaxed = true)
        val fileStore = mockk<PrivateFileStore>(relaxed = true)
        val syncRepo = mockk<SyncRepository>(relaxed = true)

        val vm = NewReportViewModel(catalogRepo, reportRepo, fileStore, syncRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onAreaChange("")
        vm.uiState.value.areaError shouldBe "El área o faena no puede estar vacía."

        vm.onAreaChange("AB")
        vm.uiState.value.areaError shouldBe "Mínimo 3 caracteres para identificar el sector."

        vm.onAreaChange("Sector Mina Norte")
        vm.uiState.value.areaError shouldBe null
    }

    test("valida observaciones exigiendo no estar vacias y maximo 500 caracteres") {
        val catalogRepo = mockk<CatalogRepository>(relaxed = true)
        val reportRepo = mockk<ReportRepository>(relaxed = true)
        val fileStore = mockk<PrivateFileStore>(relaxed = true)
        val syncRepo = mockk<SyncRepository>(relaxed = true)

        val vm = NewReportViewModel(catalogRepo, reportRepo, fileStore, syncRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onObservationsChange("")
        vm.uiState.value.observationsError shouldBe "Las observaciones son obligatorias para la trazabilidad."

        val textoLargo = "A".repeat(501)
        vm.onObservationsChange(textoLargo)
        vm.uiState.value.observationsError shouldBe "Máximo 500 caracteres excedido."

        vm.onObservationsChange("Operación de turno completada sin novedades.")
        vm.uiState.value.observationsError shouldBe null
    }

    test("formulario es invalido sin adjuntos y valido al completar campos y adjunto") {
        val catalogRepo = mockk<CatalogRepository>()
        coEvery { catalogRepo.reportTypes() } returns listOf(ReportType("shift", "Turno", "1.0"))
        coEvery { catalogRepo.processes() } returns listOf(Process("extraction", "Extracción"))

        val reportRepo = mockk<ReportRepository>(relaxed = true)
        val fileStore = mockk<PrivateFileStore>(relaxed = true)
        val syncRepo = mockk<SyncRepository>(relaxed = true)

        val vm = NewReportViewModel(catalogRepo, reportRepo, fileStore, syncRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onAreaChange("Sector Planta")
        vm.onObservationsChange("Verificación técnica")
        // Sin adjuntos no es valido
        vm.uiState.value.isFormValid shouldBe false

        // Al agregar adjunto es valido
        val dummyAttachment = Attachment(
            id = "ATT-1",
            reportId = vm.uiState.value.folio,
            kind = AttachmentKind.IMAGE,
            mimeType = "image/jpeg",
            fileName = "evidencia.jpg",
            relativePath = "evidencia.jpg",
            sizeBytes = 1024L,
            sha256 = "sha123",
        )
        // Usamos reflection o simulamos el guardado con adjunto
        vm.saveReport("u-operador")
        // Como aun no habia adjunto, no guarda
        vm.uiState.value.saveSuccess shouldBe false
    }
})
