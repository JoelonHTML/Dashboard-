package com.dailydashboard.feature.weer

import android.content.Context
import com.dailydashboard.core.database.DashboardDatabase
import com.dailydashboard.core.database.dao.WeatherDao
import com.dailydashboard.core.database.entity.WeatherEntity
import com.dailydashboard.core.database.entity.WeatherForecastEntity
import com.dailydashboard.core.network.DashboardApi
import com.dailydashboard.core.network.DashboardApiFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface WeatherRepository {
    fun observeWeather(): Flow<WeatherState?>
    suspend fun sync(): Result<Unit>
}

internal class OfflineFirstWeatherRepository(
    private val dao: WeatherDao,
    private val api: DashboardApi,
) : WeatherRepository {

    override fun observeWeather(): Flow<WeatherState?> =
        combine(dao.observeCurrent(), dao.observeForecast()) { current, forecast ->
            current?.let {
                WeatherState(
                    currentTempCelsius = it.currentTempCelsius?.toFloat(),
                    currentDescription = it.currentDescription,
                    windSpeedKmh = it.windSpeedKmh?.toFloat(),
                    forecast = forecast.map { day ->
                        WeatherDay(
                            date = day.date,
                            maxTempCelsius = day.maxTempCelsius?.toFloat(),
                            minTempCelsius = day.minTempCelsius?.toFloat(),
                            description = day.description,
                        )
                    },
                    updatedAt = it.updatedAt,
                )
            }
        }

    override suspend fun sync(): Result<Unit> = runCatching {
        val response = api.getWeather()

        dao.upsertCurrent(
            WeatherEntity(
                currentTempCelsius = response.currentTempCelsius,
                currentDescription = response.currentDescription,
                windSpeedKmh = response.windSpeedKmh,
                updatedAt = response.updatedAt,
            ),
        )
        dao.replaceForecast(
            response.forecast.mapIndexed { index, day ->
                WeatherForecastEntity(
                    date = day.date,
                    maxTempCelsius = day.maxTempCelsius,
                    minTempCelsius = day.minTempCelsius,
                    description = day.description,
                    position = index,
                )
            },
        )
    }

    companion object {
        fun create(context: Context): WeatherRepository {
            val dao = DashboardDatabase.getInstance(context).weatherDao()
            val api = DashboardApiFactory.create(context)
            return OfflineFirstWeatherRepository(dao, api)
        }
    }
}
