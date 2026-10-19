package cl.vitairos.industrial.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cl.vitairos.industrial.R
import cl.vitairos.industrial.domain.model.ReportStatus
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.ErrorLight
import cl.vitairos.industrial.ui.theme.Niebla
import cl.vitairos.industrial.ui.theme.Oro
import cl.vitairos.industrial.ui.theme.VerdeMineral
import cl.vitairos.industrial.ui.theme.VerdeNoche

/** Estado del reporte: SIEMPRE texto + ícono, nunca solo color (guía de identidad visual). */
@Composable
fun StatusChip(status: ReportStatus, modifier: Modifier = Modifier) {
    val spec = status.spec()
    Surface(color = spec.container, contentColor = spec.content, shape = RoundedCornerShape(50), modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(spec.icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(stringResource(spec.label), style = MaterialTheme.typography.labelLarge)
        }
    }
}

private data class StatusSpec(val label: Int, val icon: ImageVector, val container: Color, val content: Color)

private fun ReportStatus.spec(): StatusSpec = when (this) {
    ReportStatus.DRAFT -> StatusSpec(R.string.status_draft, Icons.Filled.Edit, Acero, VerdeNoche)
    ReportStatus.QUEUED -> StatusSpec(R.string.status_queued, Icons.AutoMirrored.Filled.List, Oro, VerdeNoche)
    ReportStatus.SENDING -> StatusSpec(R.string.status_sending, Icons.Filled.Refresh, Cobre, VerdeNoche)
    ReportStatus.SYNCED -> StatusSpec(R.string.status_synced, Icons.Filled.Check, VerdeMineral, Niebla)
    ReportStatus.ERROR -> StatusSpec(R.string.status_error, Icons.Filled.Warning, ErrorLight, Color.White)
    ReportStatus.NEEDS_CORRECTION -> StatusSpec(R.string.status_needs_correction, Icons.Filled.Info, Oro, VerdeNoche)
}
