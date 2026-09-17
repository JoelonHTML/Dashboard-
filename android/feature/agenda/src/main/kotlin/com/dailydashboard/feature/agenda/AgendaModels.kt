package com.dailydashboard.feature.agenda

import java.time.Instant

data class AgendaEvent(
    val id: String,
    val title: String,
    val start: Instant,
    val end: Instant?,
    val location: String?,
)
