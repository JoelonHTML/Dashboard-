package com.dailydashboard.feature.systeem

data class SystemStats(
    val cpuLoadPercent: Float,
    val ramUsedPercent: Float,
    val diskUsedPercent: Float?,
    val networkDownKBps: Float?,
    val networkUpKBps: Float?,
    val uptimeSeconds: Double,
    val cpuHistory: List<Float>,
    val updatedAt: String,
) {
    val uptimeHours: Float get() = (uptimeSeconds / 3600.0).toFloat()
}
