package cl.vitairos.industrial.data.local.mapper

import cl.vitairos.industrial.data.remote.dto.CurrentWeatherDto
import cl.vitairos.industrial.domain.model.WeatherInfo

// Traducimos el codigo WMO a texto legible y vemos si el viento supera los 40 km/h para alertar al operador
fun CurrentWeatherDto.toDomain(plantName: String, isCached: Boolean = false): WeatherInfo {
    val description = when (weatherCode) {
        0 -> "Despejado"
        1, 2, 3 -> "Parcialmente nublado"
        45, 48 -> "Niebla en faena"
        51, 53, 55, 61, 63, 65 -> "Lluvia"
        71, 73, 75 -> "Nieve"
        95, 96, 99 -> "Tormenta eléctrica"
        else -> "Nublado"
    }

    // Condicion adversa si hay viento mayor a 40 km/h o clima severo
    val isAdverse = windSpeed >= 40.0 || weatherCode in listOf(65, 75, 95, 96, 99)

    return WeatherInfo(
        plantName = plantName,
        temperatureCelsius = temperature,
        conditionDescription = description,
        humidityPercent = relativeHumidity,
        windSpeedKmH = windSpeed,
        isAdverseCondition = isAdverse,
        weatherCode = weatherCode,
        isOfflineCached = isCached,
    )
}
