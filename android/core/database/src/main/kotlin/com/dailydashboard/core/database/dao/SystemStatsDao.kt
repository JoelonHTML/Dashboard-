package com.dailydashboard.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.dailydashboard.core.database.entity.SystemStatsEntity
import com.dailydashboard.core.database.entity.SystemStatsHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemStatsDao {
    @Upsert
    suspend fun upsertCurrent(stats: SystemStatsEntity)

    @Query("SELECT * FROM system_stats WHERE id = 0")
    fun observeCurrent(): Flow<SystemStatsEntity?>

    @Insert
    suspend fun insertHistoryPoint(point: SystemStatsHistoryEntity)

    @Query("SELECT * FROM system_stats_history ORDER BY timestamp DESC LIMIT :limit")
    fun observeHistory(limit: Int = 20): Flow<List<SystemStatsHistoryEntity>>

    @Query(
        "DELETE FROM system_stats_history WHERE id NOT IN " +
            "(SELECT id FROM system_stats_history ORDER BY timestamp DESC LIMIT :keep)",
    )
    suspend fun trimHistory(keep: Int = 20)
}
