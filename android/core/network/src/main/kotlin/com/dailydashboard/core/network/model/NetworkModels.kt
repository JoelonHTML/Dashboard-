package com.dailydashboard.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String)

@Serializable
data class SystemStatsResponse(
    val cpu: CpuDto,
    val memory: MemoryDto,
    val disk: DiskDto? = null,
    val network: NetworkSpeedDto? = null,
    val battery: BatteryDto? = null,
    val cpuTempCelsius: Double? = null,
    val uptimeSeconds: Double,
    val updatedAt: String,
)

@Serializable
data class BatteryDto(val percent: Int, val isCharging: Boolean)

@Serializable
data class CpuDto(val loadPercent: Double)

@Serializable
data class MemoryDto(val usedPercent: Double, val totalBytes: Long, val usedBytes: Long)

@Serializable
data class DiskDto(val usedPercent: Double, val totalBytes: Long, val usedBytes: Long)

@Serializable
data class NetworkSpeedDto(val rxSec: Long, val txSec: Long)

@Serializable
data class Fs25StatsResponse(
    val online: Boolean,
    val error: String? = null,
    val serverName: String? = null,
    val mapName: String? = null,
    val playerCount: Int? = null,
    val maxPlayers: Int? = null,
    val players: List<Fs25PlayerDto> = emptyList(),
    val dayTime: Fs25DayTimeDto? = null,
    val version: String? = null,
    val updatedAt: String,
)

@Serializable
data class Fs25DayTimeDto(val day: Int? = null, val minute: Int? = null)

@Serializable
data class Fs25PlayerDto(
    val name: String,
    val isAdmin: Boolean = false,
    val uptimeMinutes: Int? = null,
)

@Serializable
data class CalendarResponse(
    val events: List<CalendarEventDto>,
    val updatedAt: String,
)

@Serializable
data class CalendarEventDto(
    val id: String,
    val title: String,
    val start: String,
    val end: String? = null,
    val location: String? = null,
)

@Serializable
data class RemindersResponse(
    val reminders: List<ReminderDto>,
    val updatedAt: String,
)

@Serializable
data class ReminderDto(
    val id: String,
    val title: String,
    val deadline: String? = null,
    val list: String? = null,
)

@Serializable
data class NsDeparturesResponse(
    val online: Boolean,
    val error: String? = null,
    val stationCode: String? = null,
    val departures: List<NsDepartureDto> = emptyList(),
    val updatedAt: String,
)

@Serializable
data class NsDepartureDto(
    val destination: String,
    val trainType: String,
    val plannedTime: String? = null,
    val actualTime: String? = null,
    val delayMinutes: Int = 0,
    val platform: String? = null,
    val platformChanged: Boolean = false,
    val cancelled: Boolean = false,
)

@Serializable
data class WeatherResponse(
    val online: Boolean,
    val error: String? = null,
    val currentTempCelsius: Double? = null,
    val currentDescription: String? = null,
    val windSpeedKmh: Double? = null,
    val forecast: List<WeatherDayDto> = emptyList(),
    val updatedAt: String,
)

@Serializable
data class WeatherDayDto(
    val date: String,
    val maxTempCelsius: Double? = null,
    val minTempCelsius: Double? = null,
    val description: String,
)
