package com.dailydashboard.feature.systeem

data class SystemStats(
    val cpuLoadPercent: Float,
    val ramUsedPercent: Float,
    val diskUsedPercent: Float?,
    val uptimeSeconds: Double,
    val cpuHistory: List<Float>,
    val updatedAt: String,
)
