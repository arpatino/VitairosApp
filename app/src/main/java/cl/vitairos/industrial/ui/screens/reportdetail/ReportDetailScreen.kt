package cl.vitairos.industrial.ui.screens.reportdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.vitairos.industrial.data.local.files.PrivateFileStore
import cl.vitairos.industrial.domain.model.Attachment
import cl.vitairos.industrial.domain.model.AttachmentKind
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.model.SyncAttempt
import cl.vitairos.industrial.domain.model.UserRole
import cl.vitairos.industrial.domain.repository.AuthRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.domain.repository.SyncRepository
import cl.vitairos.industrial.ui.components.StatusChip
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.ErrorLight
import cl.vitairos.industrial.ui.theme.Niebla
import cl.vitairos.industrial.ui.theme.Oro
import cl.vitairos.industrial.ui.theme.VerdeMineral
import cl.vitairos.industrial.ui.theme.VerdeNoche
import cl.vitairos.industrial.ui.theme.VerdeProfundo
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportDetailScreen(
    reportId: String,
    reportRepository: ReportRepository,
    authRepository: AuthRepository,
    fileStore: PrivateFileStore,
    syncRepository: SyncRepository,
    onBack: () -> Unit = {},
) {
    val report by reportRepository.observe(reportId).collectAsStateWithLifecycle(initialValue = null)
    val attempts by reportRepository.observeAttempts(reportId).collectAsStateWithLifecycle(initialValue = emptyList())
    val currentUser by authRepository.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    // Diálogos del flujo de supervisión
    var showObserveDialog by remember { mutableStateOf(false) }
    var observationReason by remember { mutableStateOf("") }

    var showCorrectionDialog by remember { mutableStateOf(false) }
    var correctionText by remember { mutableStateOf("") }

    var showApproveDialog by remember { mutableStateOf(false) }

    val isSupervisor = currentUser?.role == UserRole.SUPERVISOR
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    // Diálogo para que el Supervisor marque como OBSERVADO
    if (showObserveDialog && report != null) {
        val target = report!!
        AlertDialog(
            onDismissRequest = { showObserveDialog = false },
            containerColor = VerdeProfundo,
            title = {
                Text(
                    text = "Observar Reporte (Auditoría Técnica)",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        text = "Indica la razón o corrección requerida para el folio ${target.id}:",
                        color = Niebla,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = observationReason,
                        onValueChange = { observationReason = it },
                        placeholder = { Text("Ej: Medición de presión no coincide con sensor; adjuntar foto del manómetro.", color = Acero) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cobre,
                            unfocusedBorderColor = VerdeMineral,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedContainerColor = VerdeNoche,
                            unfocusedContainerColor = VerdeNoche,
                        ),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val updatedObservations = buildString {
                                append(target.observations ?: "")
                                append("\n\n[SUPERVISIÓN - ${currentUser?.displayName ?: "Supervisor"}]: ")
                                append(observationReason.ifBlank { "Requiere corrección técnica por supervisión." })
                            }
                            val updatedReport = target.copy(
                                status = ReportStatus.NEEDS_CORRECTION,
                                observations = updatedObservations,
                                version = target.version + 1,
                            )
                            reportRepository.save(updatedReport)
                            showObserveDialog = false
                            observationReason = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cobre, contentColor = VerdeNoche),
                ) {
                    Text("Marcar Observado", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showObserveDialog = false }) {
                    Text("Cancelar", color = Niebla)
                }
            },
        )
    }

    // Diálogo para que el Supervisor apruebe el reporte
    if (showApproveDialog && report != null) {
        val target = report!!
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            containerColor = VerdeProfundo,
            title = {
                Text(
                    text = "Aprobar Reporte Técnico",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "¿Desea validar y aprobar el reporte ${target.id}? Se encolará para sincronización con el servidor central.",
                    color = Niebla,
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val updatedReport = target.copy(
                                status = ReportStatus.QUEUED,
                                version = target.version + 1,
                            )
                            reportRepository.save(updatedReport)
                            syncRepository.enqueue(updatedReport.id)
                            showApproveDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Oro, contentColor = VerdeNoche),
                ) {
                    Text("Aprobar y Encolar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) {
                    Text("Cancelar", color = Niebla)
                }
            },
        )
    }

    // Diálogo para que el Operador corrija el reporte observado
    if (showCorrectionDialog && report != null) {
        val target = report!!
        AlertDialog(
            onDismissRequest = { showCorrectionDialog = false },
            containerColor = VerdeProfundo,
            title = {
                Text(
                    text = "Subsanar Observación Técnica",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        text = "Agrega el detalle de la subsanación para el folio ${target.id}:",
                        color = Niebla,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = correctionText,
                        onValueChange = { correctionText = it },
                        placeholder = { Text("Ej: Se verificó el manómetro y se actualizó la lectura.", color = Acero) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Oro,
                            unfocusedBorderColor = VerdeMineral,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedContainerColor = VerdeNoche,
                            unfocusedContainerColor = VerdeNoche,
                        ),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val updatedObservations = buildString {
                                append(target.observations ?: "")
                                append("\n\n[CORRECCIÓN OPERADOR]: ")
                                append(correctionText.ifBlank { "Observación subsanada en terreno." })
                            }
                            val updatedReport = target.copy(
                                status = ReportStatus.QUEUED,
                                observations = updatedObservations,
                                version = target.version + 1,
                                attemptCount = 0,
                            )
                            reportRepository.save(updatedReport)
                            syncRepository.enqueue(updatedReport.id)
                            showCorrectionDialog = false
                            correctionText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Oro, contentColor = VerdeNoche),
                ) {
                    Text("Guardar y Reenviar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCorrectionDialog = false }) {
                    Text("Cancelar", color = Niebla)
                }
            },
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        if (report == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Oro)
            }
        } else {
            val r = report!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                // Cabecera Superior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "DETALLE TÉCNICO",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = r.id,
                                style = MaterialTheme.typography.labelSmall,
                                color = Oro,
                            )
                        }
                    }

                    // Rol actual del usuario
                    Surface(
                        color = if (isSupervisor) Cobre else VerdeMineral,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = if (isSupervisor) "SUPERVISOR" else "OPERADOR",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSupervisor) VerdeNoche else Niebla,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Contenido Scrollable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Banner de Estado Principal
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (r.status == ReportStatus.NEEDS_CORRECTION) Cobre else VerdeMineral,
                                RoundedCornerShape(8.dp),
                            ),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(
                                        text = "ESTADO DEL REPORTE:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Acero,
                                    )
                                    Text(
                                        text = "Versión ${r.version} · Folio Inmutable",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Niebla,
                                    )
                                }
                                StatusChip(status = r.status)
                            }

                            // Banner si está OBSERVADO
                            if (r.status == ReportStatus.NEEDS_CORRECTION) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Cobre.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .border(1.dp, Cobre, RoundedCornerShape(6.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Cobre)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "REPORTE OBSERVADO POR SUPERVISIÓN",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Cobre,
                                        )
                                        Text(
                                            text = "Se requiere subsanar las observaciones técnicas antes de darlo por válido.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Niebla,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Ficha Operacional
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp)),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "METADATA OPERACIONAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Oro,
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            MetadataRow(label = "Tipo de Reporte", value = "${r.typeId} (v${r.typeVersion})")
                            MetadataRow(label = "Proceso Asociado", value = r.processId)
                            MetadataRow(label = "Turno Operacional", value = r.shift ?: "No especificado")
                            MetadataRow(label = "Área / Faena", value = r.areaOrSite ?: "No indicada")
                            MetadataRow(label = "Operador Reportante", value = r.userId)
                            MetadataRow(label = "Fecha y Hora Captura", value = dateFormatter.format(Date(r.createdAtMillis)))

                            if (!r.observations.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "OBSERVACIONES REGISTRADAS:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Acero,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = VerdeNoche,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = r.observations,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Niebla,
                                        modifier = Modifier.padding(10.dp),
                                    )
                                }
                            }
                        }
                    }

                    // Trazabilidad Técnica Industrial (Device ID, Hashes, Intentos)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp)),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "TRAZABILIDAD TÉCNICA Y AUDITORÍA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Oro,
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            MetadataRow(label = "Identificador de Dispositivo", value = r.installationId ?: "DEV-VITAIROS-AND-9921")
                            MetadataRow(label = "Versión Trazable", value = "v${r.version} (Inmutable)")
                            MetadataRow(label = "Intentos de Envío", value = "${r.attemptCount} intento(s)")
                            MetadataRow(
                                label = "Última Sincronización",
                                value = if (r.lastSyncAtMillis != null) dateFormatter.format(Date(r.lastSyncAtMillis)) else "Pendiente",
                            )

                            // Historial de intentos en Room
                            if (attempts.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "REGISTRO DE INTENTOS (TABLA SYNC_ATTEMPTS):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Acero,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    attempts.forEach { att ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(VerdeNoche, RoundedCornerShape(4.dp))
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Intento #${att.attemptNumber} · ${dateFormatter.format(Date(att.startedAtMillis))}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (att.success == true) Niebla else Cobre,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Text(
                                                    text = att.message ?: "Sin detalle",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Acero,
                                                )
                                            }
                                            Icon(
                                                imageVector = if (att.success == true) Icons.Default.CheckCircle else Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = if (att.success == true) Oro else ErrorLight,
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Archivos Adjuntos y Hashes Criptográficos (SHA-256)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp)),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "ADJUNTOS Y HASHES SHA-256 (${r.attachments.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Oro,
                                )
                                Text(
                                    text = "INTEGRIDAD CRIPTOGRÁFICA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Acero,
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (r.attachments.isEmpty()) {
                                Text(
                                    text = "No se registraron archivos adjuntos para este reporte.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Acero,
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    r.attachments.forEach { att ->
                                        AttachmentDetailItem(attachment = att, fileStore = fileStore)
                                    }
                                }
                            }
                        }
                    }

                    // Panel de Supervisión y Acciones
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isSupervisor) Cobre else VerdeMineral, RoundedCornerShape(8.dp)),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "ACCIONES DE FLUJO Y SUPERVISIÓN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSupervisor) Cobre else Oro,
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isSupervisor) {
                                Text(
                                    text = "Como Supervisor de Planta, puedes auditar, observar o aprobar reportes técnicos.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Niebla,
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    // Botón Observar (Requiere corrección)
                                    Button(
                                        onClick = { showObserveDialog = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Cobre,
                                            contentColor = VerdeNoche,
                                        ),
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Observar", fontWeight = FontWeight.Bold)
                                    }

                                    // Botón Aprobar
                                    Button(
                                        onClick = { showApproveDialog = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Oro,
                                            contentColor = VerdeNoche,
                                        ),
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Aprobar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // Rol Operador
                                if (r.status == ReportStatus.NEEDS_CORRECTION) {
                                    Text(
                                        text = "Este reporte fue observado por supervisión. Haz click a continuación para registrar la subsanación y reenviarlo a cola:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Niebla,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { showCorrectionDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Oro,
                                            contentColor = VerdeNoche,
                                        ),
                                    ) {
                                        Text("Subsanar y Reenviar Reporte", fontWeight = FontWeight.Bold)
                                    }
                                } else if (r.status == ReportStatus.ERROR || r.status == ReportStatus.QUEUED) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                syncRepository.retryNow(r.id)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Oro,
                                            contentColor = VerdeNoche,
                                        ),
                                    ) {
                                        Text("Forzar Reintento de Sincronización", fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text(
                                        text = "Reporte sincronizado. Registro inmutable archivado en la base de datos Room.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Acero,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Acero)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Niebla)
    }
}

@Composable
private fun AttachmentDetailItem(
    attachment: Attachment,
    fileStore: PrivateFileStore,
) {
    val file = fileStore.fileFor(attachment.relativePath)
    val sha256 = attachment.sha256 ?: if (file.exists()) fileStore.computeSha256(file) else "N/A"
    val sizeKb = (attachment.sizeBytes / 1024.0).coerceAtLeast(0.1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VerdeNoche, RoundedCornerShape(6.dp))
            .border(1.dp, VerdeMineral, RoundedCornerShape(6.dp))
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (attachment.kind == AttachmentKind.IMAGE && file.exists()) {
                AsyncImage(
                    model = file,
                    contentDescription = attachment.fileName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, VerdeMineral, RoundedCornerShape(6.dp)),
                )
            } else {
                Surface(
                    color = VerdeMineral,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(54.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (attachment.kind == AttachmentKind.PDF) "PDF" else "IMG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Oro,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.fileName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                )
                val sizeFormatted = String.format(Locale.US, "%.1f KB", sizeKb)
                Text(
                    text = "${attachment.mimeType} · $sizeFormatted",
                    style = MaterialTheme.typography.labelSmall,
                    color = Acero,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Caja del Hash SHA-256
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeProfundo, RoundedCornerShape(4.dp))
                .padding(6.dp),
        ) {
            Text(
                text = "HASH CRIPTOGRÁFICO SHA-256:",
                style = MaterialTheme.typography.labelSmall,
                color = Oro,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = sha256,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelSmall,
                color = Niebla,
            )
        }
    }
}
