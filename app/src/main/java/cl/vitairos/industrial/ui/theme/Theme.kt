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
    onPrimaryContainer = Acero,
    secondary = Cobre,
    onSecondary = Acero,
    secondaryContainer = Oro,
    onSecondaryContainer = Acero,
    tertiary = Oro,
    onTertiary = Acero,
    background = VerdeNoche,
    onBackground = Hueso,
    surface = VerdeProfundo,
    onSurface = Acero,
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
    onSurface = Acero,
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
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = VitairosTypography,
        shapes = VitairosShapes,
        content = content,
    )
}
