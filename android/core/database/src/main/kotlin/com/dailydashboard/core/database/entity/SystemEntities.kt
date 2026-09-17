package com.dailydashboard.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Altijd id=0: enkel de laatst bekende snapshot, offline-first weergegeven. */
@Entity(tableName = "system_stats")
data class SystemStatsEntity(
    @PrimaryKey val id: Int = 0,
    val cpuLoadPercent: Double,
    val ramUsedPercent: Double,
    val diskUsedPercent: Double?,
    val rxSecBytes: Long?,
    val txSecBytes: Long?,
    val uptimeSeconds: Double,
    val updatedAt: String,
)

/** Korte historie voor de CPU-ChartCard — de repository trimt dit tot de laatste 20 punten. */
@Entity(tableName = "system_stats_history")
data class SystemStatsHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cpuLoadPercent: Double,
    val timestamp: Long,
)
