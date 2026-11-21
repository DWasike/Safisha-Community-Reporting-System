package com.example.safisha.data.repository

import com.example.safisha.data.remote.RetrofitClient
import com.example.safisha.data.remote.WeatherApi
import kotlin.coroutines.cancellation.CancellationException

data class WeatherSummary(
    val temperatureC: Double,
    val precipitationMm: Double,
    val windKmh: Double,
    val description: String,
    val advice: String
)

class WeatherRepository(private val api: WeatherApi = RetrofitClient.weatherApi) {

    // Nairobi
    private val latitude = -1.2921
    private val longitude = 36.8219

    suspend fun fetchCurrentWeather(): Result<WeatherSummary> {
        return try {
            val current = api.getCurrentWeather(latitude, longitude).current
                ?: return Result.failure(IllegalStateException("Empty response"))
            val rain = current.precipitation ?: 0.0
            Result.success(
                WeatherSummary(
                    temperatureC = current.temperature ?: 0.0,
                    precipitationMm = rain,
                    windKmh = current.windSpeed ?: 0.0,
                    description = describe(current.weatherCode ?: -1),
                    advice = if (rain > 0.5) "Rain now: expect drainage and flooding reports"
                    else "Dry conditions"
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)   // IOException = offline/timeout; others = server/parse errors
        }
    }

    private fun describe(code: Int) = when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Fog"
        in 51..57 -> "Drizzle"
        in 61..67 -> "Rain"
        in 80..82 -> "Rain showers"
        in 95..99 -> "Thunderstorm"
        else -> "Unsettled"
    }
}