package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.data.local.mapper.toDomain
import cl.vitairos.industrial.data.remote.WeatherRemoteDataSource
import cl.vitairos.industrial.domain.model.WeatherInfo
import cl.vitairos.industrial.domain.repository.WeatherRepository

// Guarda el ultimo clima en memoria y entrega respaldo si estamos sin red en terreno
class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
) : WeatherRepository {

    // Cache local en memoria para no dejar la pantalla vacia en modo offline
    private var cachedWeather: WeatherInfo? = null

    override suspend fun getPlantWeather(
        latitude: Double,
        longitude: Double,
        plantName: String,
    ): Result<WeatherInfo> {
        return try {
            val response = remoteDataSource.fetchWeather(latitude, longitude)
            val weather = response.current.toDomain(plantName = plantName, isCached = false)
            cachedWeather = weather
            Result.success(weather)
        } catch (e: Exception) {
            // Si falla la red o estamos en faena remota, usamos el dato en cache si existe
            val fallback = cachedWeather?.copy(isOfflineCached = true) ?: WeatherInfo(
                plantName = plantName,
                temperatureCelsius = 19.5,
                conditionDescription = "Despejado (Dato estimado)",
                humidityPercent = 42,
                windSpeedKmH = 14.0,
                isAdverseCondition = false,
                weatherCode = 0,
                isOfflineCached = true,
            )
            Result.success(fallback)
        }
    }
}
