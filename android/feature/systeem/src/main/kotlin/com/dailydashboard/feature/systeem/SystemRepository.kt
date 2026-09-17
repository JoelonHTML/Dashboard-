package com.dailydashboard.feature.systeem

import android.content.Context
import com.dailydashboard.core.database.DashboardDatabase
import com.dailydashboard.core.database.dao.SystemStatsDao
import com.dailydashboard.core.database.entity.SystemStatsEntity
import com.dailydashboard.core.database.entity.SystemStatsHistoryEntity
import com.dailydashboard.core.network.DashboardApi
import com.dailydashboard.core.network.DashboardApiFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface SystemRepository {
    fun observeSystemStats(): Flow<SystemStats?>
    suspend fun sync(): Result<Unit>
}

/** Room is source of truth; sync() haalt de backend op en schrijft het resultaat weg. */
internal class OfflineFirstSystemRepository(
    private val dao: SystemStatsDao,
    private val api: DashboardApi,
) : SystemRepository {

    override fun observeSystemStats(): Flow<SystemStats?> {
        return combineCurrentAndHistory(dao)
    }

    override suspend fun sync(): Result<Unit> = runCatching {
        val response = api.getSystemStats()

        dao.upsertCurrent(
            SystemStatsEntity(
                cpuLoadPercent = response.cpu.loadPercent,
                ramUsedPercent = response.memory.usedPercent,
                diskUsedPercent = response.disk?.usedPercent,
                rxSecBytes = response.network?.rxSec,
                txSecBytes = response.network?.txSec,
                batteryPercent = response.battery?.percent,
                batteryCharging = response.battery?.isCharging,
                cpuTempCelsius = response.cpuTempCelsius,
                uptimeSeconds = response.uptimeSeconds,
                updatedAt = response.updatedAt,
            ),
        )
        dao.insertHistoryPoint(
            SystemStatsHistoryEntity(
                cpuLoadPercent = response.cpu.loadPercent,
                timestamp = System.currentTimeMillis(),
            ),
        )
        dao.trimHistory(keep = 20)
    }

    companion object {
        fun create(context: Context): SystemRepository {
            val dao = DashboardDatabase.getInstance(context).systemStatsDao()
            val api = DashboardApiFactory.create(context)
            return OfflineFirstSystemRepository(dao, api)
        }
    }
}

private fun combineCurrentAndHistory(dao: SystemStatsDao): Flow<SystemStats?> {
    return combine(dao.observeCurrent(), dao.observeHistory(20)) { current, history ->
        current?.let {
            SystemStats(
                cpuLoadPercent = it.cpuLoadPercent.toFloat(),
                ramUsedPercent = it.ramUsedPercent.toFloat(),
                diskUsedPercent = it.diskUsedPercent?.toFloat(),
                networkDownKBps = it.rxSecBytes?.let { bytes -> bytes / 1024f },
                networkUpKBps = it.txSecBytes?.let { bytes -> bytes / 1024f },
                batteryPercent = it.batteryPercent,
                batteryCharging = it.batteryCharging ?: false,
                cpuTempCelsius = it.cpuTempCelsius?.toFloat(),
                uptimeSeconds = it.uptimeSeconds,
                cpuHistory = history.sortedBy { point -> point.timestamp }.map { point -> point.cpuLoadPercent.toFloat() },
                updatedAt = it.updatedAt,
            )
        }
    }
}
