package cl.vitairos.industrial.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.ui.components.StatusChip
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.Niebla
import cl.vitairos.industrial.ui.theme.Oro
import cl.vitairos.industrial.ui.theme.VerdeMineral
import cl.vitairos.industrial.ui.theme.VerdeNoche
import cl.vitairos.industrial.ui.theme.VerdeProfundo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    reportRepository: ReportRepository,
    onReportClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.factory(reportRepository),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            // Cabecera de la Pantalla
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "HISTORIAL DE REPORTES",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "AUDITORÍA, TRAZABILIDAD Y REGISTROS LOCALES",
                        style = MaterialTheme.typography.labelSmall,
                        color = Oro,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de Búsqueda Reactiva
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = {
                    Text(
                        text = "Buscar por folio, tipo, proceso, área o nota...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Acero,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Oro,
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda",
                                tint = Acero,
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Oro,
                    unfocusedBorderColor = VerdeMineral,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = VerdeProfundo,
                    unfocusedContainerColor = VerdeProfundo,
                ),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Barra de Filtros por Estado (Chips Horizontales)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = uiState.selectedStatus == null,
                    onClick = { viewModel.onStatusFilterSelect(null) },
                    label = { Text("Todos (${uiState.totalCount})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Oro,
                        selectedLabelColor = VerdeNoche,
                        containerColor = VerdeProfundo,
                        labelColor = Niebla,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (uiState.selectedStatus == null) Oro else VerdeMineral,
                        enabled = true,
                        selected = uiState.selectedStatus == null,
                    ),
                )

                FilterChip(
                    selected = uiState.selectedStatus == ReportStatus.SYNCED,
                    onClick = { viewModel.onStatusFilterSelect(ReportStatus.SYNCED) },
                    label = { Text("Sincronizados (${uiState.syncedCount})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VerdeMineral,
                        selectedLabelColor = Niebla,
                        containerColor = VerdeProfundo,
                        labelColor = Niebla,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (uiState.selectedStatus == ReportStatus.SYNCED) Oro else VerdeMineral,
                        enabled = true,
                        selected = uiState.selectedStatus == ReportStatus.SYNCED,
                    ),
                )

                FilterChip(
                    selected = uiState.selectedStatus == ReportStatus.NEEDS_CORRECTION,
                    onClick = { viewModel.onStatusFilterSelect(ReportStatus.NEEDS_CORRECTION) },
                    label = { Text("Observados (${uiState.correctionCount})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Cobre,
                        selectedLabelColor = VerdeNoche,
                        containerColor = VerdeProfundo,
                        labelColor = Cobre,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (uiState.selectedStatus == ReportStatus.NEEDS_CORRECTION) Cobre else VerdeMineral,
                        enabled = true,
                        selected = uiState.selectedStatus == ReportStatus.NEEDS_CORRECTION,
                    ),
                )

                FilterChip(
                    selected = uiState.selectedStatus == ReportStatus.QUEUED,
                    onClick = { viewModel.onStatusFilterSelect(ReportStatus.QUEUED) },
                    label = { Text("En cola (${uiState.queuedCount})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Oro,
                        selectedLabelColor = VerdeNoche,
                        containerColor = VerdeProfundo,
                        labelColor = Niebla,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (uiState.selectedStatus == ReportStatus.QUEUED) Oro else VerdeMineral,
                        enabled = true,
                        selected = uiState.selectedStatus == ReportStatus.QUEUED,
                    ),
                )

                if (uiState.errorCount > 0) {
                    FilterChip(
                        selected = uiState.selectedStatus == ReportStatus.ERROR,
                        onClick = { viewModel.onStatusFilterSelect(ReportStatus.ERROR) },
                        label = { Text("Con error (${uiState.errorCount})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFB00020),
                            selectedLabelColor = Color.White,
                            containerColor = VerdeProfundo,
                            labelColor = Niebla,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (uiState.selectedStatus == ReportStatus.ERROR) Oro else VerdeMineral,
                            enabled = true,
                            selected = uiState.selectedStatus == ReportStatus.ERROR,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contador de resultados visibles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "MOSTRANDO ${uiState.reports.size} REPORTE(S):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Acero,
                )
                if (uiState.searchQuery.isNotBlank() || uiState.selectedStatus != null) {
                    TextButton(onClick = { viewModel.clearFilters() }) {
                        Text(
                            text = "Limpiar filtros",
                            style = MaterialTheme.typography.labelSmall,
                            color = Oro,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Lista de Reportes
            if (uiState.reports.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp))
                        .padding(vertical = 12.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Acero,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No se encontraron reportes",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prueba modificando el texto de búsqueda o cambiando los filtros seleccionados.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Acero,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.reports, key = { it.id }) { report ->
                        HistoryReportCard(
                            report = report,
                            onClick = { onReportClick(report.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryReportCard(
    report: Report,
    onClick: () -> Unit,
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(report.createdAtMillis))

    Card(
        colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (report.status == ReportStatus.NEEDS_CORRECTION) Cobre else VerdeMineral,
                RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Cabecera de la tarjeta: Folio + Chip de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = report.id,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Oro,
                    )
                    Text(
                        text = "${report.typeId} · ${report.processId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Niebla,
                    )
                }
                StatusChip(status = report.status)
            }

            // Alerta especial si el reporte fue observado por supervisión
            if (report.status == ReportStatus.NEEDS_CORRECTION) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Cobre.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .border(1.dp, Cobre, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Cobre,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OBSERVADO POR SUPERVISIÓN (Requiere corrección técnica)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Cobre,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metadatos operacionales y de trazabilidad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeNoche, RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Faena: ${report.areaOrSite ?: "No indicada"} · Turno: ${report.shift ?: "General"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Acero,
                    )
                    Text(
                        text = "Operador: ${report.userId} · Fecha: $formattedDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = Acero,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${report.attachments.size} adjunto(s)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Oro,
                    )
                    Text(
                        text = "Versión v${report.version}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Niebla,
                    )
                }
            }

            // Observaciones de terreno (si existen)
            if (!report.observations.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nota: ${report.observations}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Niebla,
                    maxLines = 2,
                )
            }
        }
    }
}
