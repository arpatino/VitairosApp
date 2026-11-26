package cl.vitairos.industrial.data.remote

import cl.vitairos.industrial.data.remote.dto.OpenMeteoWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

// Consultamos la api publica de Open-Meteo sin necesidad de api key
class OpenMeteoWeatherDataSource : WeatherRemoteDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun fetchWeather(latitude: Double, longitude: Double): OpenMeteoWeatherResponse =
        withContext(Dispatchers.IO) {
            val endpoint = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 4000
                readTimeout = 4000
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "VITAIROS-Industrial-Android/0.1.0")
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                val responseBody = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                json.decodeFromString<OpenMeteoWeatherResponse>(responseBody)
            } else {
                // Si la api de Open-Meteo responde con error, lanzamos excepcion para manejarlo en el repo
                throw IllegalStateException("Error al consultar Open-Meteo: HTTP $responseCode")
            }
        }
}
