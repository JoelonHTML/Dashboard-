package com.dailydashboard.feature.agenda

import android.content.Context
import com.dailydashboard.core.database.DashboardDatabase
import com.dailydashboard.core.database.dao.AgendaDao
import com.dailydashboard.core.database.entity.AgendaEventEntity
import com.dailydashboard.core.network.DashboardApi
import com.dailydashboard.core.network.DashboardApiFactory
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AgendaRepository {
    fun observeEvents(): Flow<List<AgendaEvent>>
    suspend fun sync(): Result<Unit>
}

internal class OfflineFirstAgendaRepository(
    private val dao: AgendaDao,
    private val api: DashboardApi,
) : AgendaRepository {

    override fun observeEvents(): Flow<List<AgendaEvent>> = dao.observeAll().map { entities ->
        entities.mapNotNull { it.toDomainOrNull() }
    }

    override suspend fun sync(): Result<Unit> = runCatching {
        val response = api.getCalendar()
        dao.replaceAll(
            response.events.map { event ->
                AgendaEventEntity(
                    id = event.id,
                    title = event.title,
                    start = event.start,
                    end = event.end,
                    location = event.location,
                )
            },
        )
    }

    companion object {
        fun create(context: Context): AgendaRepository {
            val dao = DashboardDatabase.getInstance(context).agendaDao()
            val api = DashboardApiFactory.create(context)
            return OfflineFirstAgendaRepository(dao, api)
        }
    }
}

private fun AgendaEventEntity.toDomainOrNull(): AgendaEvent? {
    val startInstant = runCatching { Instant.parse(start) }.getOrNull() ?: return null
    val endInstant = end?.let { runCatching { Instant.parse(it) }.getOrNull() }
    return AgendaEvent(id = id, title = title, start = startInstant, end = endInstant, location = location)
}
