package com.dailydashboard.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dailydashboard.core.database.dao.AgendaDao
import com.dailydashboard.core.database.dao.Fs25Dao
import com.dailydashboard.core.database.dao.ReminderDao
import com.dailydashboard.core.database.dao.SystemStatsDao
import com.dailydashboard.core.database.entity.AgendaEventEntity
import com.dailydashboard.core.database.entity.Fs25StatsEntity
import com.dailydashboard.core.database.entity.Fs25StatsHistoryEntity
import com.dailydashboard.core.database.entity.ReminderEntity
import com.dailydashboard.core.database.entity.SystemStatsEntity
import com.dailydashboard.core.database.entity.SystemStatsHistoryEntity

@Database(
    entities = [
        SystemStatsEntity::class,
        SystemStatsHistoryEntity::class,
        Fs25StatsEntity::class,
        Fs25StatsHistoryEntity::class,
        AgendaEventEntity::class,
        ReminderEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class DashboardDatabase : RoomDatabase() {
    abstract fun systemStatsDao(): SystemStatsDao
    abstract fun fs25Dao(): Fs25Dao
    abstract fun agendaDao(): AgendaDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var instance: DashboardDatabase? = null

        fun getInstance(context: Context): DashboardDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DashboardDatabase::class.java,
                    "dashboard.db",
                ).build().also { instance = it }
            }
    }
}
