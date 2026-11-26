package cl.vitairos.industrial.ui.screens.dashboard

import cl.vitairos.industrial.createTestReport
import cl.vitairos.industrial.data.sync.ConnectivityObserver
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.model.User
import cl.vitairos.industrial.domain.model.UserRole
import cl.vitairos.industrial.domain.model.WeatherInfo
import cl.vitairos.industrial.domain.repository.AuthRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.domain.repository.WeatherRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
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
// Pruebas unitarias con Kotest para las metricas, conexion y banner del Dashboard
class DashboardViewModelSpec : FunSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    test("DashboardUiState calcula correctamente conteos de reportes en cola, sincronizados y correcciones") {
        val authRepo = mockk<AuthRepository>()
        val reportRepo = mockk<ReportRepository>()
        val connectivity = mockk<ConnectivityObserver>()
        val weatherRepo = mockk<WeatherRepository>()

        val user = User("u-operador", "Operador Faena", UserRole.OPERATOR)
        every { authRepo.currentUser } returns flowOf(user)
        every { connectivity.isOnline } returns flowOf(true)

        val reports = listOf(
            createTestReport(id = "R1", status = ReportStatus.QUEUED),
            createTestReport(id = "R2", status = ReportStatus.SENDING),
            createTestReport(id = "R3", status = ReportStatus.SYNCED),
            createTestReport(id = "R4", status = ReportStatus.SYNCED),
            createTestReport(id = "R5", status = ReportStatus.NEEDS_CORRECTION),
            createTestReport(id = "R6", status = ReportStatus.ERROR),
        )
        every { reportRepo.observeAll() } returns flowOf(reports)

        val weatherMock = WeatherInfo(
            plantName = "Faena Cordillera",
            temperatureCelsius = 22.0,
            conditionDescription = "Despejado",
            humidityPercent = 40,
            windSpeedKmH = 15.0,
            isAdverseCondition = false,
            weatherCode = 0,
        )
        coEvery { weatherRepo.getPlantWeather(any(), any(), any()) } returns Result.success(weatherMock)

        val vm = DashboardViewModel(authRepo, reportRepo, connectivity, weatherRepo)
        val collectJob = launch(UnconfinedTestDispatcher(testDispatcher.scheduler)) { vm.uiState.collect() }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        state.isOnline shouldBe true
        state.user?.displayName shouldBe "Operador Faena"
        // Queued count incluye QUEUED, SENDING, ERROR, DRAFT (3 reportes: R1, R2, R6)
        state.queuedCount shouldBe 3
        state.syncedCount shouldBe 2
        state.needsCorrectionCount shouldBe 1
        state.recentReports.size shouldBe 4
        state.weather?.temperatureCelsius shouldBe 22.0
        collectJob.cancel()
    }

    test("signOut invoca authRepository.signOut y ejecuta el callback de salida") {
        val authRepo = mockk<AuthRepository>(relaxed = true)
        val reportRepo = mockk<ReportRepository>()
        val connectivity = mockk<ConnectivityObserver>()
        every { authRepo.currentUser } returns flowOf(null)
        every { reportRepo.observeAll() } returns flowOf(emptyList())
        every { connectivity.isOnline } returns flowOf(true)

        val vm = DashboardViewModel(authRepo, reportRepo, connectivity)
        var callbackCalled = false

        vm.signOut { callbackCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { authRepo.signOut() }
        callbackCalled shouldBe true
    }
})
