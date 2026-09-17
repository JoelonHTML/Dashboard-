package com.dailydashboard.feature.weer

data class WeatherState(
    val currentTempCelsius: Float?,
    val currentDescription: String?,
    val windSpeedKmh: Float?,
    val forecast: List<WeatherDay>,
    val updatedAt: String,
)

data class WeatherDay(
    val date: String,
    val maxTempCelsius: Float?,
    val minTempCelsius: Float?,
    val description: String,
)
