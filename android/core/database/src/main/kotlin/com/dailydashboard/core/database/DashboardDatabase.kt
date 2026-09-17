package com.dailydashboard.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dailydashboard.core.database.dao.AgendaDao
import com.dailydashboard.core.database.dao.Fs25Dao
import com.dailydashboard.core.database.dao.NsDao
import com.dailydashboard.core.database.dao.ReminderDao
import com.dailydashboard.core.database.dao.SystemStatsDao
import com.dailydashboard.core.database.dao.WeatherDao
import com.dailydashboard.core.database.entity.AgendaEventEntity
import com.dailydashboard.core.database.entity.Fs25StatsEntity
import com.dailydashboard.core.database.entity.Fs25StatsHistoryEntity
import com.dailydashboard.core.database.entity.NsDepartureEntity
import com.dailydashboard.core.database.entity.ReminderEntity
import com.dailydashboard.core.database.entity.SystemStatsEntity
import com.dailydashboard.core.database.entity.SystemStatsHistoryEntity
import com.dailydashboard.core.database.entity.WeatherEntity
import com.dailydashboard.core.database.entity.WeatherForecastEntity

@Database(
    entities = [
        SystemStatsEntity::class,
        SystemStatsHistoryEntity::class,
        Fs25StatsEntity::class,
        Fs25StatsHistoryEntity::class,
        AgendaEventEntity::class,
        ReminderEntity::class,
        NsDepartureEntity::class,
        WeatherEntity::class,
        WeatherForecastEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class DashboardDatabase : RoomDatabase() {
    abstract fun systemStatsDao(): SystemStatsDao
    abstract fun fs25Dao(): Fs25Dao
    abstract fun agendaDao(): AgendaDao
    abstract fun reminderDao(): ReminderDao
    abstract fun nsDao(): NsDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: DashboardDatabase? = null

        fun getInstance(context: Context): DashboardDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DashboardDatabase::class.java,
                    "dashboard.db",
                )
                    // Nog geen productie-installs met data om te bewaren; bij een schema-
                    // wijziging simpelweg opnieuw opbouwen in plaats van migraties schrijven.
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
    }
}
