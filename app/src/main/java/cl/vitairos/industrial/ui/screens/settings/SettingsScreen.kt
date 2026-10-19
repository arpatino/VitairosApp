package cl.vitairos.industrial.ui.screens.settings

import androidx.compose.runtime.Composable
import cl.vitairos.industrial.R
import cl.vitairos.industrial.ui.components.PlaceholderScreen

@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    PlaceholderScreen(titleRes = R.string.nav_settings, onBack = onBack)
}
