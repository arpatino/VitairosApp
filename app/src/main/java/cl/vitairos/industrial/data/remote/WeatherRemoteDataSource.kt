package cl.vitairos.industrial.data.remote

import cl.vitairos.industrial.data.remote.dto.OpenMeteoWeatherResponse

// Contrato para obtener el clima actual desde un servicio remoto
interface WeatherRemoteDataSource {
    suspend fun fetchWeather(latitude: Double, longitude: Double): OpenMeteoWeatherResponse
}
