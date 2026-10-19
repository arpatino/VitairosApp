package cl.vitairos.industrial.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

// Roboto (tipografía nativa de Android): permitida por la guía para formularios móviles.
private val Base = Typography()

val VitairosTypography = Typography(
    headlineMedium = Base.headlineMedium.copy(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold),
    titleLarge = Base.titleLarge.copy(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium),
    bodyLarge = Base.bodyLarge.copy(fontFamily = FontFamily.Default),
    labelLarge = Base.labelLarge.copy(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium),
)
