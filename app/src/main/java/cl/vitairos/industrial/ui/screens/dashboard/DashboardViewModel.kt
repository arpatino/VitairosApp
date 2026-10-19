package cl.vitairos.industrial.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.vitairos.industrial.data.sync.ConnectivityObserver
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.model.User
import cl.vitairos.industrial.domain.repository.AuthRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val user: User? = null,
    val isOnline: Boolean = true,
    val queuedCount: Int = 0,
    val syncedCount: Int = 0,
    val needsCorrectionCount: Int = 0,
    val recentReports: List<Report> = emptyList(),
)

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val reportRepository: ReportRepository,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        authRepository.currentUser,
        connectivityObserver.isOnline,
        reportRepository.observeAll(),
    ) { user, isOnline, allReports ->
        DashboardUiState(
            user = user,
            isOnline = isOnline,
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
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DashboardViewModel(authRepository, reportRepository, connectivityObserver) as T
        }
    }
}

