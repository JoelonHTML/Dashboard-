package com.dailydashboard.feature.herinneringen

import java.time.Instant

data class ReminderItem(
    val id: String,
    val title: String,
    val deadline: Instant?,
    val list: String?,
)
