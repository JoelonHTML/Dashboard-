package com.dailydashboard.app.widgetgrid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.datastore.model.WidgetConfig
import com.dailydashboard.feature.agenda.AgendaWidgetContent
import com.dailydashboard.feature.fs25.Fs25WidgetContent
import com.dailydashboard.feature.herinneringen.ReminderWidgetContent
import com.dailydashboard.feature.ns.NsWidgetContent
import com.dailydashboard.feature.systeem.SystemWidgetContent
import com.dailydashboard.feature.weer.WeatherWidgetContent

/** Koppelt elke widget aan zijn feature-module — of, voor batterij/dagvoortgang, aan het OS zelf. */
@Composable
fun WidgetContent(widget: WidgetConfig, modifier: Modifier = Modifier) {
    when (widget.dataSource) {
        DataSource.CPU_LOAD,
        DataSource.RAM_USAGE,
        DataSource.DISK_USAGE,
        DataSource.CPU_HISTORY,
        DataSource.SERVER_LOAD,
        DataSource.RAM_GAUGE,
        DataSource.DISK_GAUGE,
        DataSource.NETWORK_DOWN,
        DataSource.NETWORK_UP,
        DataSource.UPTIME_HOURS,
        DataSource.LAPTOP_BATTERY_PERCENT,
        DataSource.LAPTOP_BATTERY_GAUGE,
        DataSource.CPU_TEMP,
        -> SystemWidgetContent(dataSource = widget.dataSource, modifier = modifier)

        DataSource.FS25_PLAYER_COUNT,
        DataSource.FS25_ACTIVITY,
        DataSource.FS25_PLAYERS,
        DataSource.FS25_MAP,
        DataSource.FS25_DAY_PROGRESS,
        -> Fs25WidgetContent(dataSource = widget.dataSource, modifier = modifier)

        DataSource.NEXT_APPOINTMENT,
        DataSource.AGENDA_TODAY,
        -> AgendaWidgetContent(dataSource = widget.dataSource, modifier = modifier)

        DataSource.TASKS_TODAY_COUNT,
        DataSource.TOP_REMINDER,
        DataSource.OPEN_REMINDERS,
        -> ReminderWidgetContent(dataSource = widget.dataSource, modifier = modifier)

        DataSource.NS_DEPARTURES -> NsWidgetContent(dataSource = widget.dataSource, modifier = modifier)

        DataSource.WEATHER_TEMP,
        DataSource.WEATHER_FORECAST,
        -> WeatherWidgetContent(dataSource = widget.dataSource, modifier = modifier)

        DataSource.DAY_PROGRESS -> DayProgressGaugeWidget(modifier = modifier)
        DataSource.BATTERY -> BatteryGaugeWidget(modifier = modifier)
        DataSource.CLOCK -> ClockCalloutWidget(modifier = modifier)
    }
}
