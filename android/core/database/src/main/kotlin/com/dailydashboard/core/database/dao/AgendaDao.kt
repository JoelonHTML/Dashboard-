package com.dailydashboard.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.dailydashboard.core.database.entity.AgendaEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaDao {
    @Upsert
    suspend fun upsertAll(events: List<AgendaEventEntity>)

    @Query("DELETE FROM agenda_events")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(events: List<AgendaEventEntity>) {
        deleteAll()
        upsertAll(events)
    }

    @Query("SELECT * FROM agenda_events ORDER BY start ASC")
    fun observeAll(): Flow<List<AgendaEventEntity>>
}
