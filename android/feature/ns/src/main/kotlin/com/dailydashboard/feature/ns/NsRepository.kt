package com.dailydashboard.feature.ns

import android.content.Context
import com.dailydashboard.core.database.DashboardDatabase
import com.dailydashboard.core.database.dao.NsDao
import com.dailydashboard.core.database.entity.NsDepartureEntity
import com.dailydashboard.core.network.DashboardApi
import com.dailydashboard.core.network.DashboardApiFactory
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface NsRepository {
    fun observeDepartures(): Flow<List<NsDeparture>>
    suspend fun sync(): Result<Unit>
}

internal class OfflineFirstNsRepository(
    private val dao: NsDao,
    private val api: DashboardApi,
) : NsRepository {

    override fun observeDepartures(): Flow<List<NsDeparture>> = dao.observeAll().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun sync(): Result<Unit> = runCatching {
        val response = api.getNsDepartures()
        dao.replaceAll(
            response.departures.mapIndexed { index, dto ->
                NsDepartureEntity(
                    destination = dto.destination,
                    trainType = dto.trainType,
                    plannedTime = dto.plannedTime,
                    actualTime = dto.actualTime,
                    delayMinutes = dto.delayMinutes,
                    platform = dto.platform,
                    platformChanged = dto.platformChanged,
                    cancelled = dto.cancelled,
                    position = index,
                )
            },
        )
    }

    companion object {
        fun create(context: Context): NsRepository {
            val dao = DashboardDatabase.getInstance(context).nsDao()
            val api = DashboardApiFactory.create(context)
            return OfflineFirstNsRepository(dao, api)
        }
    }
}

private fun NsDepartureEntity.toDomain(): NsDeparture = NsDeparture(
    id = id,
    destination = destination,
    trainType = trainType,
    plannedTime = plannedTime?.let { runCatching { Instant.parse(it) }.getOrNull() },
    actualTime = actualTime?.let { runCatching { Instant.parse(it) }.getOrNull() },
    delayMinutes = delayMinutes,
    platform = platform,
    platformChanged = platformChanged,
    cancelled = cancelled,
)
