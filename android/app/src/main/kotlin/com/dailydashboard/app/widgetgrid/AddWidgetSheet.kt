package com.dailydashboard.app.widgetgrid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.datastore.model.WidgetType

private val widgetTypeLabels = mapOf(
    WidgetType.STAT_CARD to "KPI-kaartje",
    WidgetType.CHART_CARD to "Grafiek",
    WidgetType.GAUGE_CARD to "Meter",
    WidgetType.CALLOUT_CARD to "Uitgelicht",
    WidgetType.LIST_CARD to "Lijst",
)

private val dataSourceLabels = mapOf(
    DataSource.CPU_LOAD to "CPU-gebruik",
    DataSource.RAM_USAGE to "RAM-gebruik",
    DataSource.DISK_USAGE to "Schijfruimte",
    DataSource.FS25_PLAYER_COUNT to "FS25-spelersaantal",
    DataSource.TASKS_TODAY_COUNT to "Taken vandaag",
    DataSource.NETWORK_DOWN to "Downloadsnelheid",
    DataSource.NETWORK_UP to "Uploadsnelheid",
    DataSource.UPTIME_HOURS to "Uptime laptop",
    DataSource.CPU_HISTORY to "CPU-historie",
    DataSource.FS25_ACTIVITY to "FS25-activiteit",
    DataSource.SERVER_LOAD to "Serverbelasting",
    DataSource.DAY_PROGRESS to "Dagvoortgang",
    DataSource.BATTERY to "Batterij (tablet)",
    DataSource.RAM_GAUGE to "Geheugengebruik (meter)",
    DataSource.DISK_GAUGE to "Schijfgebruik (meter)",
    DataSource.NEXT_APPOINTMENT to "Volgende afspraak",
    DataSource.TOP_REMINDER to "Belangrijkste herinnering",
    DataSource.FS25_MAP to "FS25-actieve map",
    DataSource.AGENDA_TODAY to "Agenda vandaag",
    DataSource.OPEN_REMINDERS to "Openstaande herinneringen",
    DataSource.FS25_PLAYERS to "FS25-spelerslijst",
    DataSource.LAPTOP_BATTERY_PERCENT to "Batterij laptop",
    DataSource.CPU_TEMP to "CPU-temperatuur",
    DataSource.WEATHER_TEMP to "Weer — temperatuur",
    DataSource.LAPTOP_BATTERY_GAUGE to "Batterij laptop (meter)",
    DataSource.FS25_DAY_PROGRESS to "FS25-dagvoortgang",
    DataSource.CLOCK to "Klok",
    DataSource.NS_DEPARTURES to "Trein — Lage Zwaluwe",
    DataSource.WEATHER_FORECAST to "Weersverwachting",
)

/** Kiezer met alle vijf widget-types; per type alleen de databronnen die erbij passen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWidgetSheet(
    onDismiss: () -> Unit,
    onWidgetChosen: (DataSource) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(text = "Widget toevoegen", style = MaterialTheme.typography.titleMedium)

            WidgetType.entries.forEach { type ->
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(text = widgetTypeLabels.getValue(type), style = MaterialTheme.typography.bodyMedium)
                    DataSource.forWidgetType(type).forEach { source ->
                        TextButton(
                            onClick = { onWidgetChosen(source) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = dataSourceLabels[source] ?: source.name,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Annuleren")
            }
        }
    }
}
