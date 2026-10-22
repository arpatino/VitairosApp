package cl.vitairos.industrial.ui.screens.newreport

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.vitairos.industrial.domain.repository.CatalogRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.ui.components.PrimaryButton
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Hueso
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.Niebla
import cl.vitairos.industrial.ui.theme.Oro
import cl.vitairos.industrial.ui.theme.VerdeMineral
import cl.vitairos.industrial.ui.theme.VerdeNoche
import cl.vitairos.industrial.ui.theme.VerdeProfundo
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    catalogRepository: CatalogRepository,
    reportRepository: ReportRepository,
    activeUserId: String,
    onReportSaved: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewReportViewModel = viewModel(
        factory = NewReportViewModel.factory(catalogRepository, reportRepository),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var typeExpanded by remember { mutableStateOf(false) }
    var processExpanded by remember { mutableStateOf(false) }
    var shiftExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            delay(1200)
            onReportSaved()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            // Cabecera con Botón de Regreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Hueso,
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "REGISTRO DE REPORTE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Hueso,
                    )
                    Text(
                        text = "PASO 1: METADATA Y TRAZABILIDAD LOCAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Oro,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Banner Animado de Éxito al Guardar
            AnimatedVisibility(
                visible = uiState.saveSuccess,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, Oro, RoundedCornerShape(8.dp)),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Oro, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reporte ${uiState.folio} persistido en base Room local. Redirigiendo...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Acero,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Formulario en Contenedor Scrollable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Folio Inmutable
                Card(
                    colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "FOLIO OPERACIONAL TRAZABLE (AUTO)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Acero,
                            )
                            Text(
                                text = uiState.folio,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Oro,
                            )
                        }
                        Surface(
                            color = VerdeNoche,
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VerdeMineral),
                        ) {
                            Text(
                                text = "UUID INMUTABLE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Acero,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }

                // Selector Tipo de Reporte (Catálogo cerrado)
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                ) {
                    val currentType = uiState.reportTypes.firstOrNull { it.id == uiState.selectedTypeId }?.name ?: "Seleccione tipo..."
                    OutlinedTextField(
                        value = currentType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Reporte (Catálogo Cerrado *)", color = Hueso, style = MaterialTheme.typography.bodySmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Oro,
                            unfocusedBorderColor = VerdeMineral,
                            focusedTextColor = Acero,
                            unfocusedTextColor = Acero,
                            focusedLabelColor = Hueso,
                            unfocusedLabelColor = Hueso,
                        ),
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false },
                        modifier = Modifier.background(VerdeProfundo),
                    ) {
                        uiState.reportTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name, color = Acero) },
                                onClick = {
                                    viewModel.onTypeSelect(type.id)
                                    typeExpanded = false
                                },
                            )
                        }
                    }
                }

                // Selector Proceso Asociado (Lista fija)
                ExposedDropdownMenuBox(
                    expanded = processExpanded,
                    onExpandedChange = { processExpanded = !processExpanded },
                ) {
                    val currentProcess = uiState.processes.firstOrNull { it.id == uiState.selectedProcessId }?.name ?: "Seleccione proceso..."
                    OutlinedTextField(
                        value = currentProcess,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Proceso Asociado (Lista Fija *)", color = Hueso, style = MaterialTheme.typography.bodySmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = processExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Oro,
                            unfocusedBorderColor = VerdeMineral,
                            focusedTextColor = Acero,
                            unfocusedTextColor = Acero,
                            focusedLabelColor = Hueso,
                            unfocusedLabelColor = Hueso,
                        ),
                    )

                    ExposedDropdownMenu(
                        expanded = processExpanded,
                        onDismissRequest = { processExpanded = false },
                        modifier = Modifier.background(VerdeProfundo),
                    ) {
                        uiState.processes.forEach { proc ->
                            DropdownMenuItem(
                                text = { Text(proc.name, color = Acero) },
                                onClick = {
                                    viewModel.onProcessSelect(proc.id)
                                    processExpanded = false
                                },
                            )
                        }
                    }
                }

                // Selector Turno
                ExposedDropdownMenuBox(
                    expanded = shiftExpanded,
                    onExpandedChange = { shiftExpanded = !shiftExpanded },
                ) {
                    OutlinedTextField(
                        value = uiState.shift,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Turno Operacional *", color = Hueso, style = MaterialTheme.typography.bodySmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shiftExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Oro,
                            unfocusedBorderColor = VerdeMineral,
                            focusedTextColor = Acero,
                            unfocusedTextColor = Acero,
                            focusedLabelColor = Hueso,
                            unfocusedLabelColor = Hueso,
                        ),
                    )

                    ExposedDropdownMenu(
                        expanded = shiftExpanded,
                        onDismissRequest = { shiftExpanded = false },
                        modifier = Modifier.background(VerdeProfundo),
                    ) {
                        listOf("Turno 1 (Mañana)", "Turno 2 (Tarde)", "Turno 3 (Noche)").forEach { shiftOption ->
                            DropdownMenuItem(
                                text = { Text(shiftOption, color = Acero) },
                                onClick = {
                                    viewModel.onShiftSelect(shiftOption)
                                    shiftExpanded = false
                                },
                            )
                        }
                    }
                }

                // Campo Área / Faena (Validado campo por campo)
                Column {
                    OutlinedTextField(
                        value = uiState.areaOrSite,
                        onValueChange = viewModel::onAreaChange,
                        label = { Text("Área / Faena Específica *", color = Hueso, style = MaterialTheme.typography.bodySmall) },
                        placeholder = { Text("Ej: Planta Concentradora - Correa 04", color = Acero) },
                        singleLine = true,
                        isError = uiState.areaError != null,
                        trailingIcon = {
                            if (uiState.areaError != null) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = Cobre)
                            } else if (uiState.areaOrSite.isNotBlank()) {
                                Icon(Icons.Default.Check, contentDescription = "Válido", tint = Niebla)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.areaError != null) Cobre else Oro,
                            unfocusedBorderColor = if (uiState.areaError != null) Cobre else VerdeMineral,
                            errorBorderColor = Cobre,
                            focusedTextColor = Acero,
                            unfocusedTextColor = Acero,
                            focusedLabelColor = Hueso,
                            unfocusedLabelColor = Hueso,
                        ),
                    )

                    // Animación de Retroalimentación de Error
                    AnimatedVisibility(
                        visible = uiState.areaError != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        uiState.areaError?.let { err ->
                            Text(
                                text = err,
                                style = MaterialTheme.typography.labelSmall,
                                color = Cobre,
                                modifier = Modifier.padding(start = 6.dp, top = 4.dp),
                            )
                        }
                    }
                }

                // Campo Observaciones (Validado campo por campo con contador)
                Column {
                    OutlinedTextField(
                        value = uiState.observations,
                        onValueChange = viewModel::onObservationsChange,
                        label = { Text("Observaciones de Campo *", color = Hueso, style = MaterialTheme.typography.bodySmall) },
                        placeholder = { Text("Ingrese notas sobre anomalías o inspección...", color = Acero) },
                        minLines = 3,
                        maxLines = 5,
                        isError = uiState.observationsError != null,
                        trailingIcon = {
                            if (uiState.observationsError != null) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = Cobre)
                            } else if (uiState.observations.isNotBlank()) {
                                Icon(Icons.Default.Check, contentDescription = "Válido", tint = Niebla)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.observationsError != null) Cobre else Oro,
                            unfocusedBorderColor = if (uiState.observationsError != null) Cobre else VerdeMineral,
                            errorBorderColor = Cobre,
                            focusedTextColor = Acero,
                            unfocusedTextColor = Acero,
                            focusedLabelColor = Hueso,
                            unfocusedLabelColor = Hueso,
                        ),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        AnimatedVisibility(
                            visible = uiState.observationsError != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                        ) {
                            uiState.observationsError?.let { err ->
                                Text(
                                    text = err,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Cobre,
                                )
                            }
                        }

                        Text(
                            text = "${uiState.observations.length} / 500",
                            style = MaterialTheme.typography.labelSmall,
                            color = Acero,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón de Envío / Guardado
            if (uiState.isSaving) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Cobre)
                }
            } else {
                PrimaryButton(
                    text = "GUARDAR EN COLA LOCAL (ROOM)",
                    onClick = { viewModel.saveReport(activeUserId) },
                    enabled = uiState.isFormValid,
                )
            }
        }
    }
}
