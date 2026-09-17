package com.dailydashboard.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.dailydashboard.core.database.entity.WeatherEntity
import com.dailydashboard.core.database.entity.WeatherForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Upsert
    suspend fun upsertCurrent(weather: WeatherEntity)

    @Query("SELECT * FROM weather_current WHERE id = 0")
    fun observeCurrent(): Flow<WeatherEntity?>

    @Upsert
    suspend fun upsertForecast(days: List<WeatherForecastEntity>)

    @Query("DELETE FROM weather_forecast")
    suspend fun deleteForecast()

    @Transaction
    suspend fun replaceForecast(days: List<WeatherForecastEntity>) {
        deleteForecast()
        upsertForecast(days)
    }

    @Query("SELECT * FROM weather_forecast ORDER BY position ASC")
    fun observeForecast(): Flow<List<WeatherForecastEntity>>
}
