package cl.vitairos.industrial.ui.screens.dashboard

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.vitairos.industrial.R
import cl.vitairos.industrial.data.sync.ConnectivityObserver
import cl.vitairos.industrial.domain.model.Report
import cl.vitairos.industrial.domain.model.UserRole
import cl.vitairos.industrial.domain.repository.AuthRepository
import cl.vitairos.industrial.domain.repository.ReportRepository
import cl.vitairos.industrial.ui.components.StatusChip
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Hueso
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.Niebla
import cl.vitairos.industrial.ui.theme.Oro
import cl.vitairos.industrial.ui.theme.VerdeMineral
import cl.vitairos.industrial.ui.theme.VerdeNoche
import cl.vitairos.industrial.ui.theme.VerdeProfundo

@Composable
fun DashboardScreen(
    authRepository: AuthRepository,
    reportRepository: ReportRepository,
    connectivityObserver: ConnectivityObserver,
    onNavigateToNewReport: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.factory(authRepository, reportRepository, connectivityObserver),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            // Cabecera Superior con Marca y Cierre de Sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.vitairos_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, VerdeMineral, RoundedCornerShape(6.dp))
                            .padding(2.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "VITAIROS INDUSTRIAL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Hueso,
                        )
                        Text(
                            text = "TERMINAL OPERACIONAL // DSY1105",
                            style = MaterialTheme.typography.labelSmall,
                            color = Oro,
                        )
                    }
                }

                IconButton(onClick = { viewModel.signOut(onSignedOut) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = Acero,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Banner de Conectividad en Tiempo Real (Recurso Nativo: ConnectivityManager)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeProfundo, RoundedCornerShape(8.dp))
                    .border(1.dp, if (uiState.isOnline) VerdeMineral else Cobre, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isOnline) Oro else Cobre),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isOnline) "ENLACE ACTIVO · SYNC EN SEGUNDO PLANO" else "MODO OFFLINE (FAENA) · PERSISTENCIA EN ROOM",
                        style = MaterialTheme.typography.labelSmall,
                        color = Acero,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = if (uiState.isOnline) "4G LTE" else "SIN RED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (uiState.isOnline) Oro else Cobre,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tarjeta de Usuario Activo y Telemetría
            Card(
                colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VerdeMineral, RoundedCornerShape(10.dp)),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column {
                            Text(
                                text = "OPERADOR EN FAENA",
                                style = MaterialTheme.typography.labelSmall,
                                color = Acero,
                            )
                            Text(
                                text = uiState.user?.displayName ?: "Juan Faena",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Acero,
                            )
                            Text(
                                text = "${uiState.user?.role ?: UserRole.OPERATOR} // Turno 1 (Mañana)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Oro,
                            )
                        }
                        Surface(
                            color = VerdeNoche,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VerdeMineral),
                        ) {
                            Text(
                                text = "DEV-9921",
                                style = MaterialTheme.typography.labelSmall,
                                color = Acero,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Botones Principales de Acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = onNavigateToNewReport,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Cobre,
                                contentColor = VerdeNoche,
                            ),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NUEVO REPORTE",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Button(
                            onClick = onNavigateToQueue,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Oro,
                                contentColor = VerdeNoche,
                            ),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COLA (${uiState.queuedCount})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Métricas de Persistencia Local (Room SQLite)
            Text(
                text = "REGISTROS EN DISPOSITIVO (ROOM DB):",
                style = MaterialTheme.typography.labelSmall,
                color = Acero,
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricCard(
                    title = "EN COLA",
                    count = uiState.queuedCount,
                    accentColor = Oro,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToQueue,
                )
                MetricCard(
                    title = "SYNC OK",
                    count = uiState.syncedCount,
                    accentColor = Niebla,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToHistory,
                )
                MetricCard(
                    title = "OBSERVADOS",
                    count = uiState.needsCorrectionCount,
                    accentColor = Cobre,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToHistory,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lista de Reportes Recientes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ACTIVIDAD RECIENTE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Acero,
                )
                Text(
                    text = "HISTORIAL COMPLETO →",
                    style = MaterialTheme.typography.labelSmall,
                    color = Oro,
                    modifier = Modifier.clickable(onClick = onNavigateToHistory),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (uiState.recentReports.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                ) {
                    Text(
                        text = "No hay reportes registrados aún en la base de datos local.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Acero,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.recentReports) { report ->
                        ReportItemRow(report = report)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    count: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VerdeProfundo),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .border(1.dp, VerdeMineral, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Acero,
            )
        }
    }
}

@Composable
private fun ReportItemRow(report: Report) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.id,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Acero,
                )
                Text(
                    text = "${report.typeId} · ${report.processId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Niebla,
                )
            }
            StatusChip(status = report.status)
        }
    }
}
