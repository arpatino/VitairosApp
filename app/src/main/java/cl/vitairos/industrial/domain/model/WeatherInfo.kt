package cl.vitairos.industrial.domain.model

// Modelo simple para representar el clima de la faena en la interfaz
data class WeatherInfo(
    val plantName: String,
    val temperatureCelsius: Double,
    val conditionDescription: String,
    val humidityPercent: Int,
    val windSpeedKmH: Double,
    val isAdverseCondition: Boolean,
    val weatherCode: Int,
    val isOfflineCached: Boolean = false,
)
