package cl.vitairos.industrial.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cl.vitairos.industrial.R
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.Niebla

/** Contenido temporal de cada pantalla mientras no se implemente. */
@Composable
fun PlaceholderScreen(
    titleRes: Int,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.placeholder_screen),
                style = MaterialTheme.typography.bodyLarge,
                color = Acero,
            )
            if (onBack != null) {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Cobre,
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text("Volver", color = Niebla)
                }
            }
        }
    }
}
