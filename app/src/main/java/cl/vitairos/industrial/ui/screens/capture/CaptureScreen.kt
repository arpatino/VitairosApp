package cl.vitairos.industrial.ui.screens.capture

import androidx.compose.runtime.Composable
import cl.vitairos.industrial.R
import cl.vitairos.industrial.ui.components.PlaceholderScreen

@Composable
fun CaptureScreen(onBack: () -> Unit = {}) {
    PlaceholderScreen(titleRes = R.string.nav_capture, onBack = onBack)
}
