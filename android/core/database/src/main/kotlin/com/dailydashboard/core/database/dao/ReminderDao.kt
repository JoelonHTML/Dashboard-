package com.dailydashboard.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.dailydashboard.core.database.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Upsert
    suspend fun upsertAll(reminders: List<ReminderEntity>)

    @Query("DELETE FROM reminders")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(reminders: List<ReminderEntity>) {
        deleteAll()
        upsertAll(reminders)
    }

    @Query("SELECT * FROM reminders ORDER BY deadline ASC")
    fun observeAll(): Flow<List<ReminderEntity>>
}
