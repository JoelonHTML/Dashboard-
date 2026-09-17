package com.dailydashboard.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Altijd id=0: enkel de laatst bekende weersituatie. */
@Entity(tableName = "weather_current")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val currentTempCelsius: Double?,
    val currentDescription: String?,
    val windSpeedKmh: Double?,
    val updatedAt: String,
)

@Entity(tableName = "weather_forecast")
data class WeatherForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val maxTempCelsius: Double?,
    val minTempCelsius: Double?,
    val description: String,
    val position: Int,
)
