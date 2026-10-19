package cl.vitairos.industrial.ui.screens.history

import androidx.compose.runtime.Composable
import cl.vitairos.industrial.R
import cl.vitairos.industrial.ui.components.PlaceholderScreen

@Composable
fun HistoryScreen(onBack: () -> Unit = {}) {
    PlaceholderScreen(titleRes = R.string.nav_history, onBack = onBack)
}
