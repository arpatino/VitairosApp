package cl.vitairos.industrial.ui.screens.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.vitairos.industrial.data.remote.MockReportApi
import cl.vitairos.industrial.data.sync.RetryPolicy
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.domain.repository.SyncRepository
import cl.vitairos.industrial.ui.components.SimulationBadge
import cl.vitairos.industrial.ui.components.StatusChip
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.Niebla
import cl.vitairos.industrial.ui.theme.Oro
import cl.vitairos.industrial.ui.theme.VerdeMineral
import cl.vitairos.industrial.ui.theme.VerdeNoche
import cl.vitairos.industrial.ui.theme.VerdeProfundo
import kotlinx.coroutines.launch

@Composable
fun QueueScreen(
    reportRepository: ReportRepository,
    syncRepository: SyncRepository,
    onReportClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val queueReports by reportRepository.observeQueue().collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()

    // Estado para activar/desactivar la conexión a Vercel Cloud vs Mock Local
    var useVercelCloud by remember { mutableStateOf(MockReportApi.useVercelCloud) }

    // Estado para activar/desactivar la simulación de fallo en la API (Backoff 5s, 15s, 30s)
    var simulateFailure by remember { mutableStateOf(MockReportApi.simulateFailure) }

    // Reporte seleccionado para confirmar eliminación de la cola
    var reportToDelete by remember { mutableStateOf<Report?>(null) }

    if (reportToDelete != null) {
        val target = reportToDelete!!
        AlertDialog(
            onDismissRequest = { reportToDelete = null },
            containerColor = VerdeProfundo,
            title = {
                Text(
                    text = "Confirmar eliminación",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "¿Desea eliminar el reporte ${target.id} que aún se encuentra en la cola local?",
                    color = Niebla,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            reportRepository.delete(target.id)
                            reportToDelete = null
                        }
                    },
                ) {
                    Text("Eliminar", color = Cobre, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { reportToDelete = null }) {
                    Text("Cancelar", color = Niebla)
                }
            },
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            // Cabecera Superior con Botón de Regreso
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "COLA DE SINCRONIZACIÓN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "WORKMANAGER + BACKOFF EXPONENCIAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = Oro,
                        )
                    }
                }
                SimulationBadge()
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta de Conexión API Mock Vercel
            Card(
                colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (useVercelCloud) Oro else VerdeMineral, RoundedCornerShape(8.dp)),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CONEXIÓN API MOCK EN VERCEL",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (useVercelCloud) Oro else Acero,
                            )
                            Text(
                                text = if (useVercelCloud) {
                                    "Activo: Despacha peticiones HTTP reales a Vercel Cloud."
                                } else {
                                    "Inactivo: Usando Mock local en memoria."
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Niebla,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = useVercelCloud,
                            onCheckedChange = { checked ->
                                useVercelCloud = checked
                                MockReportApi.useVercelCloud = checked
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VerdeNoche,
                                checkedTrackColor = Oro,
                                uncheckedThumbColor = Acero,
                                uncheckedTrackColor = VerdeNoche,
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Endpoint: ${MockReportApi.vercelApiUrl}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall,
                        color = Acero,
                    )
                    Text(
                        text = "Estado: ${MockReportApi.lastConnectionStatus}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (MockReportApi.lastSyncInCloud) Oro else Niebla,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de Modo Prueba para demostrar el Backoff (5s, 15s, 30s) y paso a ERROR
            Card(
                colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (simulateFailure) Cobre else VerdeMineral, RoundedCornerShape(8.dp)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MODO PRUEBA: SIMULAR FALLO DE RED",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (simulateFailure) Cobre else Oro,
                        )
                        Text(
                            text = if (simulateFailure) {
                                "Activo: Los envíos fallarán para demostrar los 3 reintentos (5s, 15s, 30s) y el estado ERROR."
                            } else {
                                "Inactivo: Los reportes en cola se sincronizarán normalmente."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Niebla,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = simulateFailure,
                        onCheckedChange = { checked ->
                            simulateFailure = checked
                            MockReportApi.simulateFailure = checked
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VerdeNoche,
                            checkedTrackColor = Cobre,
                            uncheckedThumbColor = Acero,
                            uncheckedTrackColor = VerdeNoche,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "REPORTES EN COLA O PENDIENTES (${queueReports.size}):",
                style = MaterialTheme.typography.labelSmall,
                color = Acero,
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (queueReports.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp)),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "No hay reportes pendientes en la cola.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Todos los reportes han sido procesados y sincronizados exitosamente.",
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
                    items(queueReports, key = { it.id }) { report ->
                        QueueReportCard(
                            report = report,
                            onReportClick = { onReportClick(report.id) },
                            onRetryManual = {
                                scope.launch {
                                    syncRepository.retryNow(report.id)
                                }
                            },
                            onDeleteClick = {
                                reportToDelete = report
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueReportCard(
    report: Report,
    onReportClick: () -> Unit,
    onRetryManual: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val nextDelay = RetryPolicy.delaySecondsFor(report.attemptCount)

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
            .clickable(onClick = onReportClick),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "${report.typeId} · ${report.processId} · ${report.shift ?: ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Niebla,
                    )
                }
                StatusChip(status = report.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información de adjuntos y estado de reintentos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeNoche, RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Adjuntos: ${report.attachments.size} archivo(s) · Versión v${report.version}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Oro,
                    )
                    val retryInfoText = when (report.status) {
                        ReportStatus.SENDING -> "Despachando a servidor VITAIROS (Intento ${report.attemptCount + 1})..."
                        ReportStatus.ERROR -> "Agotados los ${RetryPolicy.MAX_AUTOMATIC_RETRIES} reintentos automáticos (5s, 15s, 30s)."
                        ReportStatus.NEEDS_CORRECTION -> "Observado por supervisión. Haz click para ver las observaciones."
                        ReportStatus.QUEUED -> {
                            if (report.attemptCount == 0) {
                                "En cola esperando turno de envío..."
                            } else {
                                "Intento ${report.attemptCount} falló · Próximo reintento tras ${nextDelay ?: 30}s"
                            }
                        }
                        else -> "Intentos realizados: ${report.attemptCount}"
                    }
                    Text(
                        text = retryInfoText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (report.status == ReportStatus.ERROR || report.status == ReportStatus.NEEDS_CORRECTION) Cobre else Acero,
                    )
                }

                if (report.status == ReportStatus.SENDING) {
                    CircularProgressIndicator(
                        color = Oro,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Botones de acción
            if (report.status != ReportStatus.SENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Botón para ir al detalle técnico
                    OutlinedButton(
                        onClick = onReportClick,
                        shape = RoundedCornerShape(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Niebla,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DETALLE", style = MaterialTheme.typography.labelSmall, color = Niebla)
                    }

                    Row {
                        if (report.status.isEditable) {
                            TextButton(onClick = onDeleteClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Acero,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ELIMINAR",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Acero,
                                )
                            }
                        }

                        if (report.status == ReportStatus.ERROR || report.status == ReportStatus.QUEUED) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = onRetryManual,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (report.status == ReportStatus.ERROR) Cobre else Oro,
                                    contentColor = VerdeNoche,
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "REINTENTAR",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
