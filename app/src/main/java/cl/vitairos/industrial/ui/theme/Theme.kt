package cl.vitairos.industrial.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = VerdeProfundo,
    onPrimary = Hueso,
    primaryContainer = Niebla,
    onPrimaryContainer = VerdeNoche,
    secondary = Cobre,
    onSecondary = VerdeNoche,
    secondaryContainer = Oro,
    onSecondaryContainer = VerdeNoche,
    tertiary = Oro,
    onTertiary = VerdeNoche,
    background = Hueso,
    onBackground = VerdeNoche,
    surface = Hueso,
    onSurface = VerdeNoche,
    surfaceVariant = Niebla,
    onSurfaceVariant = VerdeMineral,
    outline = Acero,
    error = ErrorLight,
    onError = Hueso,
)

private val DarkColors = darkColorScheme(
    primary = Cobre,
    onPrimary = VerdeNoche,
    primaryContainer = VerdeMineral,
    onPrimaryContainer = Niebla,
    secondary = Oro,
    onSecondary = VerdeNoche,
    tertiary = Acero,
    onTertiary = VerdeNoche,
    background = VerdeNoche,
    onBackground = Hueso,
    surface = VerdeProfundo,
    onSurface = Hueso,
    surfaceVariant = VerdeMineral,
    onSurfaceVariant = Niebla,
    outline = Acero,
    error = ErrorDark,
    onError = VerdeNoche,
)

@Composable
fun VitairosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Sin colores dinámicos: se respeta siempre la identidad visual de VITAIROS.
    MaterialTheme(
        colorScheme = if (darkTheme) LightColors else DarkColors,
        typography = VitairosTypography,
        shapes = VitairosShapes,
        content = content,
    )
}
