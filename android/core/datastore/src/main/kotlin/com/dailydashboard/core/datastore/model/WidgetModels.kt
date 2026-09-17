package com.dailydashboard.core.datastore.model

import kotlinx.serialization.Serializable

/** De vijf herbruikbare widget-kaarten uit het design system. */
@Serializable
enum class WidgetType {
    STAT_CARD,
    CHART_CARD,
    GAUGE_CARD,
    CALLOUT_CARD,
    LIST_CARD,
}

/** Formaat binnen de grid: klein/breed/groot. */
@Serializable
enum class WidgetSize {
    SMALL,
    WIDE,
    LARGE,
}

/**
 * Elke databron hoort bij precies één widget-type — de widget-kiezer filtert
 * hierop zodat je geen onzinnige combinatie (bv. een lijst in een gauge) kan kiezen.
 */
@Serializable
enum class DataSource(val widgetType: WidgetType) {
    CPU_LOAD(WidgetType.STAT_CARD),
    RAM_USAGE(WidgetType.STAT_CARD),
    DISK_USAGE(WidgetType.STAT_CARD),
    FS25_PLAYER_COUNT(WidgetType.STAT_CARD),
    TASKS_TODAY_COUNT(WidgetType.STAT_CARD),
    NETWORK_DOWN(WidgetType.STAT_CARD),
    NETWORK_UP(WidgetType.STAT_CARD),
    UPTIME_HOURS(WidgetType.STAT_CARD),
    LAPTOP_BATTERY_PERCENT(WidgetType.STAT_CARD),
    CPU_TEMP(WidgetType.STAT_CARD),
    WEATHER_TEMP(WidgetType.STAT_CARD),

    CPU_HISTORY(WidgetType.CHART_CARD),
    FS25_ACTIVITY(WidgetType.CHART_CARD),

    SERVER_LOAD(WidgetType.GAUGE_CARD),
    DAY_PROGRESS(WidgetType.GAUGE_CARD),
    BATTERY(WidgetType.GAUGE_CARD),
    RAM_GAUGE(WidgetType.GAUGE_CARD),
    DISK_GAUGE(WidgetType.GAUGE_CARD),
    LAPTOP_BATTERY_GAUGE(WidgetType.GAUGE_CARD),
    FS25_DAY_PROGRESS(WidgetType.GAUGE_CARD),

    NEXT_APPOINTMENT(WidgetType.CALLOUT_CARD),
    TOP_REMINDER(WidgetType.CALLOUT_CARD),
    FS25_MAP(WidgetType.CALLOUT_CARD),
    CLOCK(WidgetType.CALLOUT_CARD),

    AGENDA_TODAY(WidgetType.LIST_CARD),
    OPEN_REMINDERS(WidgetType.LIST_CARD),
    FS25_PLAYERS(WidgetType.LIST_CARD),
    NS_DEPARTURES(WidgetType.LIST_CARD),
    WEATHER_FORECAST(WidgetType.LIST_CARD),
    ;

    companion object {
        fun forWidgetType(type: WidgetType): List<DataSource> = entries.filter { it.widgetType == type }
    }
}

@Serializable
data class WidgetConfig(
    val id: String,
    val type: WidgetType,
    val size: WidgetSize,
    val dataSource: DataSource,
    val position: Int,
)
