package cl.vitairos.industrial.ui.screens.history

import cl.vitairos.industrial.createTestReport
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.repository.ReportRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
// Pruebas unitarias con Kotest para la busqueda, filtros y eliminacion en el Historial
class HistoryViewModelSpec : FunSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val sampleReports = listOf(
        createTestReport(
            id = "REP-2026-CHANCADO",
            typeId = "shift",
            processId = "extraction",
            userId = "u-operador",
            areaOrSite = "Chancado Primario",
            observations = "Novedad en correa 01",
            status = ReportStatus.SYNCED,
        ),
        createTestReport(
            id = "REP-2026-TALLER",
            typeId = "maintenance",
            processId = "maintenance",
            userId = "u-supervisor",
            areaOrSite = "Taller Camiones",
            observations = "Cambio de filtro hidráulico",
            status = ReportStatus.QUEUED,
        ),
        createTestReport(
            id = "REP-2026-SEGURIDAD",
            typeId = "incident",
            processId = "safety",
            userId = "u-operador",
            areaOrSite = "Polvorín",
            observations = "Inspección de extintores",
            status = ReportStatus.NEEDS_CORRECTION,
        ),
    )

    test("filtra reportes segun la consulta de busqueda por folio, area o proceso") {
        val repo = mockk<ReportRepository>()
        every { repo.observeAll() } returns flowOf(sampleReports)
        val vm = HistoryViewModel(repo)
        val collectJob = launch(UnconfinedTestDispatcher(testDispatcher.scheduler)) { vm.uiState.collect() }
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.value.totalCount shouldBe 3
        vm.uiState.value.reports.size shouldBe 3

        // Busqueda por area
        vm.onSearchQueryChange("chancado")
        testDispatcher.scheduler.advanceUntilIdle()
        vm.uiState.value.reports.size shouldBe 1
        vm.uiState.value.reports.first().id shouldBe "REP-2026-CHANCADO"

        // Busqueda por tipo
        vm.onSearchQueryChange("maintenance")
        testDispatcher.scheduler.advanceUntilIdle()
        vm.uiState.value.reports.size shouldBe 1
        vm.uiState.value.reports.first().id shouldBe "REP-2026-TALLER"

        collectJob.cancel()
    }

    test("filtra por estado y proceso, y clearFilters restablece la lista completa") {
        val repo = mockk<ReportRepository>()
        every { repo.observeAll() } returns flowOf(sampleReports)
        val vm = HistoryViewModel(repo)
        val collectJob = launch(UnconfinedTestDispatcher(testDispatcher.scheduler)) { vm.uiState.collect() }
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onStatusFilterSelect(ReportStatus.SYNCED)
        testDispatcher.scheduler.advanceUntilIdle()
        vm.uiState.value.reports.size shouldBe 1
        vm.uiState.value.reports.first().status shouldBe ReportStatus.SYNCED

        // Limpiar filtros
        vm.clearFilters()
        testDispatcher.scheduler.advanceUntilIdle()
        vm.uiState.value.reports.size shouldBe 3
        vm.uiState.value.selectedStatus shouldBe null
        vm.uiState.value.searchQuery shouldBe ""

        collectJob.cancel()
    }

    test("deleteReport delega en el repositorio de reportes") {
        val repo = mockk<ReportRepository>(relaxed = true)
        every { repo.observeAll() } returns flowOf(emptyList())
        val vm = HistoryViewModel(repo)

        vm.deleteReport("REP-DELETE-ME")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repo.delete("REP-DELETE-ME") }
    }
})
