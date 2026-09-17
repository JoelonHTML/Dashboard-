package com.dailydashboard.core.network

import com.dailydashboard.core.network.model.CalendarResponse
import com.dailydashboard.core.network.model.Fs25StatsResponse
import com.dailydashboard.core.network.model.HealthResponse
import com.dailydashboard.core.network.model.RemindersResponse
import com.dailydashboard.core.network.model.SystemStatsResponse
import retrofit2.http.GET

interface DashboardApi {
    @GET("/api/health")
    suspend fun getHealth(): HealthResponse

    @GET("/api/system")
    suspend fun getSystemStats(): SystemStatsResponse

    @GET("/api/fs25")
    suspend fun getFs25Stats(): Fs25StatsResponse

    @GET("/api/calendar")
    suspend fun getCalendar(): CalendarResponse

    @GET("/api/reminders")
    suspend fun getReminders(): RemindersResponse
}
