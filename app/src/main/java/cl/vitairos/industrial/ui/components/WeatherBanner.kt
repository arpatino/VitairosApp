package cl.vitairos.industrial.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.vitairos.industrial.domain.model.WeatherInfo
import cl.vitairos.industrial.ui.theme.Acero
import cl.vitairos.industrial.ui.theme.Cobre
import cl.vitairos.industrial.ui.theme.Hueso
import cl.vitairos.industrial.ui.theme.Niebla
import cl.vitairos.industrial.ui.theme.Oro
import cl.vitairos.industrial.ui.theme.VerdeMineral
import cl.vitairos.industrial.ui.theme.VerdeProfundo

// Banner menor para mostrar el clima en faena sin saturar la pantalla principal
@Composable
fun WeatherBanner(
    weather: WeatherInfo?,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = when {
        weather?.isAdverseCondition == true -> Cobre
        weather != null -> VerdeMineral
        else -> Acero
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VerdeProfundo, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (weather != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                // Indicador circular de estado climatico
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (weather.isAdverseCondition) Cobre else Oro),
                )
                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Temperatura y planta
                        Text(
                            text = "${weather.temperatureCelsius}°C",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Hueso,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "·  ${weather.plantName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Niebla,
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${weather.conditionDescription}  ·  Viento ${weather.windSpeedKmH} km/h",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = if (weather.isAdverseCondition) Cobre else Acero,
                        )

                        if (weather.isOfflineCached) {
                            Text(
                                text = " (offline)",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = Oro,
                            )
                        }
                    }
                }
            }
        } else {
            // Estado cuando todavia esta conectando con Open-Meteo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Acero),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sincronizando clima de faena (Open-Meteo)...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Niebla,
                )
            }
        }

        // Boton para refrescar el clima o spinner si esta cargando
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Oro,
                strokeWidth = 2.dp,
            )
        } else {
            IconButton(
                onClick = onRefresh,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar clima",
                    tint = Acero,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
