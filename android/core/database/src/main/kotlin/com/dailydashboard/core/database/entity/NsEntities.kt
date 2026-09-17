package com.dailydashboard.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ns_departures")
data class NsDepartureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val destination: String,
    val trainType: String,
    val plannedTime: String?,
    val actualTime: String?,
    val delayMinutes: Int,
    val platform: String?,
    val platformChanged: Boolean,
    val cancelled: Boolean,
    val position: Int,
)
