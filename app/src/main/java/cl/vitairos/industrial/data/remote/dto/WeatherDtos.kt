package cl.vitairos.industrial.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Dto para parsear la respuesta JSON que entrega la api de Open-Meteo
@Serializable
data class OpenMeteoWeatherResponse(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val current: CurrentWeatherDto = CurrentWeatherDto(),
)

// Variables que nos interesan para la faena: temperatura, humedad, codigo de clima y viento
@Serializable
data class CurrentWeatherDto(
    val time: String = "",
    @SerialName("temperature_2m") val temperature: Double = 0.0,
    @SerialName("relative_humidity_2m") val relativeHumidity: Int = 0,
    @SerialName("weather_code") val weatherCode: Int = 0,
    @SerialName("wind_speed_10m") val windSpeed: Double = 0.0,
)
