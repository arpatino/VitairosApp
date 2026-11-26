package cl.vitairos.industrial.data.remote

import cl.vitairos.industrial.data.local.mapper.toDomain
import cl.vitairos.industrial.data.remote.dto.CurrentWeatherDto
import cl.vitairos.industrial.data.remote.dto.OpenMeteoWeatherResponse
import cl.vitairos.industrial.data.repository.WeatherRepositoryImpl
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

// Pruebas unitarias con Kotest para la integracion del clima con Open-Meteo
class WeatherServiceAndMapperSpec : FunSpec({

    test("WeatherMapper traduce codigos WMO y detecta viento normal") {
        val dto = CurrentWeatherDto(
            temperature = 22.5,
            relativeHumidity = 35,
            weatherCode = 0, // Despejado
            windSpeed = 15.0, // Viento moderado
        )

        val domain = dto.toDomain(plantName = "Faena Cordillera")
        domain.plantName shouldBe "Faena Cordillera"
        domain.temperatureCelsius shouldBe 22.5
        domain.conditionDescription shouldBe "Despejado"
        domain.humidityPercent shouldBe 35
        domain.windSpeedKmH shouldBe 15.0
        domain.isAdverseCondition shouldBe false
        domain.isOfflineCached shouldBe false
    }

    test("WeatherMapper marca alerta de condicion adversa si viento supera los 40 km/h o tormenta") {
        val dtoViento = CurrentWeatherDto(
            temperature = 14.0,
            relativeHumidity = 50,
            weatherCode = 2, // Nublado
            windSpeed = 45.0, // Viento fuerte > 40
        )
        val domainViento = dtoViento.toDomain(plantName = "Mina Norte")
        domainViento.isAdverseCondition shouldBe true

        val dtoTormenta = CurrentWeatherDto(
            temperature = 10.0,
            relativeHumidity = 80,
            weatherCode = 95, // Tormenta electrica
            windSpeed = 20.0,
        )
        val domainTormenta = dtoTormenta.toDomain(plantName = "Mina Sur")
        domainTormenta.conditionDescription shouldBe "Tormenta eléctrica"
        domainTormenta.isAdverseCondition shouldBe true
    }

    test("WeatherRepositoryImpl retorna el clima de la faena si la llamada a Open-Meteo es exitosa") {
        val remoteDataSource = mockk<WeatherRemoteDataSource>()
        coEvery { remoteDataSource.fetchWeather(any(), any()) } returns OpenMeteoWeatherResponse(
            latitude = -23.65,
            longitude = -70.40,
            current = CurrentWeatherDto(
                temperature = 21.0,
                relativeHumidity = 40,
                weatherCode = 1,
                windSpeed = 12.0,
            )
        )

        val repository = WeatherRepositoryImpl(remoteDataSource)
        val result = repository.getPlantWeather()

        result.isSuccess shouldBe true
        val info = result.getOrNull()
        info?.temperatureCelsius shouldBe 21.0
        info?.conditionDescription shouldBe "Parcialmente nublado"
        info?.isOfflineCached shouldBe false
    }

    test("WeatherRepositoryImpl entrega datos de respaldo con isOfflineCached=true si falla la conexion") {
        val remoteDataSource = mockk<WeatherRemoteDataSource>()
        coEvery { remoteDataSource.fetchWeather(any(), any()) } throws RuntimeException("Sin enlace")

        val repository = WeatherRepositoryImpl(remoteDataSource)
        val result = repository.getPlantWeather(plantName = "Faena Desconectada")

        result.isSuccess shouldBe true
        val info = result.getOrNull()
        info?.plantName shouldBe "Faena Desconectada"
        info?.isOfflineCached shouldBe true
    }
})
