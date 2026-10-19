package cl.vitairos.industrial.ui.screens.newreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.vitairos.industrial.domain.model.Process
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.model.ReportType
import cl.vitairos.industrial.domain.repository.CatalogRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class NewReportUiState(
    val folio: String = "REP-2026-" + UUID.randomUUID().toString().take(6).uppercase(),
    val reportTypes: List<ReportType> = emptyList(),
    val selectedTypeId: String = "",
    val processes: List<Process> = emptyList(),
    val selectedProcessId: String = "",
    val shift: String = "Turno 1 (Mañana)",
    val areaOrSite: String = "",
    val observations: String = "",
    val areaError: String? = null,
    val observationsError: String? = null,
    val isFormValid: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
)

class NewReportViewModel(
    private val catalogRepository: CatalogRepository,
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewReportUiState())
    val uiState: StateFlow<NewReportUiState> = _uiState.asStateFlow()

    init {
        loadCatalogs()
    }

    private fun loadCatalogs() {
        viewModelScope.launch {
            val types = catalogRepository.reportTypes()
            val procs = catalogRepository.processes()
            _uiState.update {
                it.copy(
                    reportTypes = types,
                    selectedTypeId = types.firstOrNull()?.id.orEmpty(),
                    processes = procs,
                    selectedProcessId = procs.firstOrNull()?.id.orEmpty(),
                )
            }
            validateForm()
        }
    }

    fun onAreaChange(area: String) {
        val error = when {
            area.isBlank() -> "El área o faena no puede estar vacía."
            area.trim().length < 3 -> "Mínimo 3 caracteres para identificar el sector."
            else -> null
        }
        _uiState.update { it.copy(areaOrSite = area, areaError = error) }
        validateForm()
    }

    fun onObservationsChange(obs: String) {
        val error = when {
            obs.isBlank() -> "Las observaciones son obligatorias para la trazabilidad."
            obs.length > 500 -> "Máximo 500 caracteres excedido."
            else -> null
        }
        _uiState.update { it.copy(observations = obs, observationsError = error) }
        validateForm()
    }

    fun onTypeSelect(typeId: String) {
        _uiState.update { it.copy(selectedTypeId = typeId) }
        validateForm()
    }

    fun onProcessSelect(processId: String) {
        _uiState.update { it.copy(selectedProcessId = processId) }
        validateForm()
    }

    fun onShiftSelect(shift: String) {
        _uiState.update { it.copy(shift = shift) }
    }

    private fun validateForm() {
        val current = _uiState.value
        val isValid = current.areaOrSite.isNotBlank() &&
            current.areaOrSite.trim().length >= 3 &&
            current.observations.isNotBlank() &&
            current.selectedTypeId.isNotBlank() &&
            current.selectedProcessId.isNotBlank() &&
            current.areaError == null &&
            current.observationsError == null

        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun saveReport(userId: String) {
        validateForm()
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val current = _uiState.value
            val newReport = Report(
                id = current.folio,
                typeId = current.selectedTypeId,
                typeVersion = "1.0",
                processId = current.selectedProcessId,
                createdAtMillis = System.currentTimeMillis(),
                userId = userId,
                areaOrSite = current.areaOrSite.trim(),
                shift = current.shift,
                observations = current.observations.trim(),
                status = ReportStatus.QUEUED, // Queda encolado en Room para despacho
                attachments = emptyList(),
                attemptCount = 0,
                lastSyncAtMillis = null,
                version = 1,
                installationId = "DEV-VITAIROS-AND-9921",
            )

            reportRepository.save(newReport)
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    companion object {
        fun factory(
            catalogRepository: CatalogRepository,
            reportRepository: ReportRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                NewReportViewModel(catalogRepository, reportRepository) as T
        }
    }
}

