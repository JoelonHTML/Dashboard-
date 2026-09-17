package com.dailydashboard.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.dailydashboard.core.database.entity.NsDepartureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NsDao {
    @Upsert
    suspend fun upsertAll(departures: List<NsDepartureEntity>)

    @Query("DELETE FROM ns_departures")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(departures: List<NsDepartureEntity>) {
        deleteAll()
        upsertAll(departures)
    }

    @Query("SELECT * FROM ns_departures ORDER BY position ASC")
    fun observeAll(): Flow<List<NsDepartureEntity>>
}
