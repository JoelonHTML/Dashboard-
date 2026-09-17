package com.dailydashboard.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Altijd id=0: enkel de laatst bekende FS25-status. */
@Entity(tableName = "fs25_stats")
data class Fs25StatsEntity(
    @PrimaryKey val id: Int = 0,
    val online: Boolean,
    val serverName: String?,
    val mapName: String?,
    val playerCount: Int?,
    val maxPlayers: Int?,
    /** JSON-array van spelersnamen ["Naam1","Naam2"] — geen aparte tabel nodig voor dit lijstje. */
    val playerNamesJson: String,
    /** Minuten sinds het begin van de in-game dag (0..1439), voor de dagvoortgang-gauge. */
    val inGameDayMinute: Int?,
    val version: String?,
    val updatedAt: String,
)

/** Korte historie van het spelersaantal voor de FS25-activiteit-ChartCard. */
@Entity(tableName = "fs25_stats_history")
data class Fs25StatsHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playerCount: Int,
    val timestamp: Long,
)
