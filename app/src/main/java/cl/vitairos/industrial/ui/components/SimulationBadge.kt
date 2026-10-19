package cl.vitairos.industrial.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cl.vitairos.industrial.R

/** Etiqueta obligatoria para todo dato proveniente del mock (guía de identidad visual). */
@Composable
fun SimulationBadge(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.simulation_badge),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
