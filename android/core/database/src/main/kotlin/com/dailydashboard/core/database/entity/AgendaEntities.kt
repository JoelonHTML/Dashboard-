package com.dailydashboard.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agenda_events")
data class AgendaEventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val start: String,
    val end: String?,
    val location: String?,
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val deadline: String?,
    val list: String?,
)
