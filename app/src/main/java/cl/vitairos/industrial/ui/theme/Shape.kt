package cl.vitairos.industrial.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val VitairosShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
)

/** Alto mínimo táctil para operación en terreno (guía de interfaz móvil). */
val MinTouchTarget = 56.dp
