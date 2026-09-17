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
    val uptimeSeconds: Double,
    val updatedAt: String,
)

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
    val version: String? = null,
    val updatedAt: String,
)

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
