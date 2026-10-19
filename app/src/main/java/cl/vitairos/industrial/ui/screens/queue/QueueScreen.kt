package cl.vitairos.industrial.ui.screens.queue

import androidx.compose.runtime.Composable
import cl.vitairos.industrial.R
import cl.vitairos.industrial.ui.components.PlaceholderScreen

@Composable
fun QueueScreen(onBack: () -> Unit = {}) {
    PlaceholderScreen(titleRes = R.string.nav_queue, onBack = onBack)
}
