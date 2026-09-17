package com.dailydashboard.feature.fs25

data class Fs25Stats(
    val online: Boolean,
    val serverName: String?,
    val mapName: String?,
    val playerCount: Int,
    val maxPlayers: Int,
    val playerNames: List<String>,
    val activityHistory: List<Float>,
    /** Minuten sinds het begin van de in-game dag (0..1439). */
    val inGameDayMinute: Int?,
    val updatedAt: String,
) {
    val inGameDayProgress: Float get() = (inGameDayMinute ?: 0) / 1440f
}
