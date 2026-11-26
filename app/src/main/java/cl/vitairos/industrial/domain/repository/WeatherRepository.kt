package cl.vitairos.industrial.domain.repository

import cl.vitairos.industrial.domain.model.WeatherInfo

// Contrato del repositorio para obtener el clima de la faena
interface WeatherRepository {
    suspend fun getPlantWeather(
        latitude: Double = -23.65,
        longitude: Double = -70.40,
        plantName: String = "Faena Cordillera",
    ): Result<WeatherInfo>
}
