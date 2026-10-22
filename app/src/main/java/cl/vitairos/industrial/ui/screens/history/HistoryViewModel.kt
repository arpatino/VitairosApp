package cl.vitairos.industrial.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val searchQuery: String = "",
    val selectedStatus: ReportStatus? = null,
    val selectedProcess: String? = null,
    val reports: List<Report> = emptyList(),
    val totalCount: Int = 0,
    val syncedCount: Int = 0,
    val queuedCount: Int = 0,
    val correctionCount: Int = 0,
    val errorCount: Int = 0,
)

class HistoryViewModel(
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<ReportStatus?>(null)
    private val _selectedProcess = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HistoryUiState> = combine(
        reportRepository.observeAll(),
        _searchQuery,
        _selectedStatus,
        _selectedProcess,
    ) { allReports, query, status, process ->
        val filtered = allReports.filter { report ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                report.id.lowercase().contains(q) ||
                    report.typeId.lowercase().contains(q) ||
                    report.processId.lowercase().contains(q) ||
                    (report.areaOrSite?.lowercase()?.contains(q) == true) ||
                    (report.observations?.lowercase()?.contains(q) == true) ||
                    report.userId.lowercase().contains(q)
            }

            val matchesStatus = status == null || report.status == status
            val matchesProcess = process == null || report.processId == process

            matchesQuery && matchesStatus && matchesProcess
        }

        HistoryUiState(
            searchQuery = query,
            selectedStatus = status,
            selectedProcess = process,
            reports = filtered,
            totalCount = allReports.size,
            syncedCount = allReports.count { it.status == ReportStatus.SYNCED },
            queuedCount = allReports.count { it.status == ReportStatus.QUEUED || it.status == ReportStatus.SENDING },
            correctionCount = allReports.count { it.status == ReportStatus.NEEDS_CORRECTION },
            errorCount = allReports.count { it.status == ReportStatus.ERROR },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(),
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.update { newQuery }
    }

    fun onStatusFilterSelect(status: ReportStatus?) {
        _selectedStatus.update { if (it == status) null else status }
    }

    fun onProcessFilterSelect(processId: String?) {
        _selectedProcess.update { if (it == processId) null else processId }
    }

    fun clearFilters() {
        _searchQuery.update { "" }
        _selectedStatus.update { null }
        _selectedProcess.update { null }
    }

    fun deleteReport(reportId: String) {
        viewModelScope.launch {
            reportRepository.delete(reportId)
        }
    }

    companion object {
        fun factory(reportRepository: ReportRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    HistoryViewModel(reportRepository) as T
            }
    }
}

