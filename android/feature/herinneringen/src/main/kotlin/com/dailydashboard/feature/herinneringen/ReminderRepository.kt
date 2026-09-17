package com.dailydashboard.feature.herinneringen

import android.content.Context
import com.dailydashboard.core.database.DashboardDatabase
import com.dailydashboard.core.database.dao.ReminderDao
import com.dailydashboard.core.database.entity.ReminderEntity
import com.dailydashboard.core.network.DashboardApi
import com.dailydashboard.core.network.DashboardApiFactory
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ReminderRepository {
    fun observeReminders(): Flow<List<ReminderItem>>
    suspend fun sync(): Result<Unit>
}

internal class OfflineFirstReminderRepository(
    private val dao: ReminderDao,
    private val api: DashboardApi,
) : ReminderRepository {

    override fun observeReminders(): Flow<List<ReminderItem>> = dao.observeAll().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun sync(): Result<Unit> = runCatching {
        val response = api.getReminders()
        dao.replaceAll(
            response.reminders.map { reminder ->
                ReminderEntity(
                    id = reminder.id,
                    title = reminder.title,
                    deadline = reminder.deadline,
                    list = reminder.list,
                )
            },
        )
    }

    companion object {
        fun create(context: Context): ReminderRepository {
            val dao = DashboardDatabase.getInstance(context).reminderDao()
            val api = DashboardApiFactory.create(context)
            return OfflineFirstReminderRepository(dao, api)
        }
    }
}

private fun ReminderEntity.toDomain(): ReminderItem {
    val deadlineInstant = deadline?.let { runCatching { Instant.parse(it) }.getOrNull() }
    return ReminderItem(id = id, title = title, deadline = deadlineInstant, list = list)
}
