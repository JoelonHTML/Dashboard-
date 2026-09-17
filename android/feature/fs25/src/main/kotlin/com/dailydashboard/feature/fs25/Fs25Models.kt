package com.dailydashboard.feature.fs25

data class Fs25Stats(
    val online: Boolean,
    val serverName: String?,
    val mapName: String?,
    val playerCount: Int,
    val maxPlayers: Int,
    val playerNames: List<String>,
    val activityHistory: List<Float>,
    val updatedAt: String,
)
