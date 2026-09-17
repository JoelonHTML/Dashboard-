package com.dailydashboard.feature.fs25

import android.content.Context
import com.dailydashboard.core.database.DashboardDatabase
import com.dailydashboard.core.database.dao.Fs25Dao
import com.dailydashboard.core.database.entity.Fs25StatsEntity
import com.dailydashboard.core.database.entity.Fs25StatsHistoryEntity
import com.dailydashboard.core.network.DashboardApi
import com.dailydashboard.core.network.DashboardApiFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

interface Fs25Repository {
    fun observeFs25Stats(): Flow<Fs25Stats?>
    suspend fun sync(): Result<Unit>
}

internal class OfflineFirstFs25Repository(
    private val dao: Fs25Dao,
    private val api: DashboardApi,
) : Fs25Repository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeFs25Stats(): Flow<Fs25Stats?> {
        return combine(dao.observeCurrent(), dao.observeHistory(20)) { current, history ->
            current?.let {
                Fs25Stats(
                    online = it.online,
                    serverName = it.serverName,
                    mapName = it.mapName,
                    playerCount = it.playerCount ?: 0,
                    maxPlayers = it.maxPlayers ?: 0,
                    playerNames = runCatching { json.decodeFromString<List<String>>(it.playerNamesJson) }.getOrDefault(emptyList()),
                    activityHistory = history.sortedBy { point -> point.timestamp }.map { point -> point.playerCount.toFloat() },
                    inGameDayMinute = it.inGameDayMinute,
                    updatedAt = it.updatedAt,
                )
            }
        }
    }

    override suspend fun sync(): Result<Unit> = runCatching {
        val response = api.getFs25Stats()

        dao.upsertCurrent(
            Fs25StatsEntity(
                online = response.online,
                serverName = response.serverName,
                mapName = response.mapName,
                playerCount = response.playerCount,
                maxPlayers = response.maxPlayers,
                playerNamesJson = json.encodeToString(response.players.map { it.name }),
                inGameDayMinute = response.dayTime?.minute,
                version = response.version,
                updatedAt = response.updatedAt,
            ),
        )
        dao.insertHistoryPoint(
            Fs25StatsHistoryEntity(
                playerCount = response.playerCount ?: 0,
                timestamp = System.currentTimeMillis(),
            ),
        )
        dao.trimHistory(keep = 20)
    }

    companion object {
        fun create(context: Context): Fs25Repository {
            val dao = DashboardDatabase.getInstance(context).fs25Dao()
            val api = DashboardApiFactory.create(context)
            return OfflineFirstFs25Repository(dao, api)
        }
    }
}
