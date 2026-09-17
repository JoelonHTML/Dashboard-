package com.dailydashboard.feature.ns

import java.time.Instant

data class NsDeparture(
    val id: Long,
    val destination: String,
    val trainType: String,
    val plannedTime: Instant?,
    val actualTime: Instant?,
    val delayMinutes: Int,
    val platform: String?,
    val platformChanged: Boolean,
    val cancelled: Boolean,
)
