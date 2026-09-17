package com.dailydashboard.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.dailydashboard.core.database.entity.Fs25StatsEntity
import com.dailydashboard.core.database.entity.Fs25StatsHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface Fs25Dao {
    @Upsert
    suspend fun upsertCurrent(stats: Fs25StatsEntity)

    @Query("SELECT * FROM fs25_stats WHERE id = 0")
    fun observeCurrent(): Flow<Fs25StatsEntity?>

    @Insert
    suspend fun insertHistoryPoint(point: Fs25StatsHistoryEntity)

    @Query("SELECT * FROM fs25_stats_history ORDER BY timestamp DESC LIMIT :limit")
    fun observeHistory(limit: Int = 20): Flow<List<Fs25StatsHistoryEntity>>

    @Query(
        "DELETE FROM fs25_stats_history WHERE id NOT IN " +
            "(SELECT id FROM fs25_stats_history ORDER BY timestamp DESC LIMIT :keep)",
    )
    suspend fun trimHistory(keep: Int = 20)
}
