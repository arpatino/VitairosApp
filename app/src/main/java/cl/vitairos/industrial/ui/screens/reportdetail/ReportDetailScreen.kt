package cl.vitairos.industrial.ui.screens.reportdetail

import androidx.compose.runtime.Composable
import cl.vitairos.industrial.R
import cl.vitairos.industrial.ui.components.PlaceholderScreen

@Composable
fun ReportDetailScreen(onBack: () -> Unit = {}) {
    PlaceholderScreen(titleRes = R.string.nav_report_detail, onBack = onBack)
}
