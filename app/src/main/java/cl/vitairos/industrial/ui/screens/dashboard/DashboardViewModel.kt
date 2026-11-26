package cl.vitairos.industrial.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.vitairos.industrial.data.sync.ConnectivityObserver
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.model.User
import cl.vitairos.industrial.domain.model.WeatherInfo
import cl.vitairos.industrial.domain.repository.AuthRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WeatherState(
    val weather: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class DashboardUiState(
    val user: User? = null,
    val isOnline: Boolean = true,
    val weather: WeatherInfo? = null,
    val isWeatherLoading: Boolean = false,
    val weatherError: String? = null,
    val queuedCount: Int = 0,
    val syncedCount: Int = 0,
    val needsCorrectionCount: Int = 0,
    val recentReports: List<Report> = emptyList(),
)

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val reportRepository: ReportRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val weatherRepository: WeatherRepository? = null,
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherState())

    init {
        // Consultamos el clima apenas el operador entra al dashboard
        refreshWeather()
    }

    // Permite actualizar el clima manualmente tocando el boton en el banner
    fun refreshWeather() {
        if (weatherRepository == null) return
        viewModelScope.launch {
            _weatherState.value = _weatherState.value.copy(isLoading = true, error = null)
            val result = weatherRepository.getPlantWeather()
            result.onSuccess { info ->
                _weatherState.value = WeatherState(weather = info, isLoading = false, error = null)
            }.onFailure { ex ->
                _weatherState.value = WeatherState(weather = null, isLoading = false, error = ex.message ?: "No se pudo obtener el clima")
            }
        }
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        authRepository.currentUser,
        connectivityObserver.isOnline,
        reportRepository.observeAll(),
        _weatherState,
    ) { user, isOnline, allReports, weatherState ->
        DashboardUiState(
            user = user,
            isOnline = isOnline,
            weather = weatherState.weather,
            isWeatherLoading = weatherState.isLoading,
            weatherError = weatherState.error,
            queuedCount = allReports.count { it.status == ReportStatus.QUEUED || it.status == ReportStatus.SENDING || it.status == ReportStatus.ERROR || it.status == ReportStatus.DRAFT },
            syncedCount = allReports.count { it.status == ReportStatus.SYNCED },
            needsCorrectionCount = allReports.count { it.status == ReportStatus.NEEDS_CORRECTION },
            recentReports = allReports.take(4),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(),
    )

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onSignedOut()
        }
    }

    companion object {
        fun factory(
            authRepository: AuthRepository,
            reportRepository: ReportRepository,
            connectivityObserver: ConnectivityObserver,
            weatherRepository: WeatherRepository? = null,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DashboardViewModel(authRepository, reportRepository, connectivityObserver, weatherRepository) as T
        }
    }
}
