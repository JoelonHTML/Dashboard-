package com.dailydashboard.feature.ns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.ui.widgets.ListCard
import com.dailydashboard.core.ui.widgets.ListRowData
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

@Composable
fun NsWidgetContent(
    dataSource: DataSource,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: NsViewModel = viewModel(factory = NsViewModel.factory(context))
    val uiState by viewModel.uiState.collectAsState()

    val departures = (uiState as? NsUiState.Success)?.departures ?: emptyList()

    when (dataSource) {
        DataSource.NS_DEPARTURES -> ListCard(
            title = "Trein — Lage Zwaluwe",
            items = departures.map { departure ->
                val time = departure.actualTime ?: departure.plannedTime
                ListRowData(
                    marker = departure.platform ?: "?",
                    primaryText = departure.destination,
                    secondaryText = departure.trainType,
                    statusText = when {
                        departure.cancelled -> "Vervalt"
                        departure.delayMinutes > 0 -> "+${departure.delayMinutes} min"
                        time != null -> timeFormatter.format(time)
                        else -> null
                    },
                    statusPositive = !departure.cancelled && departure.delayMinutes <= 0,
                )
            },
            emptyText = "Geen vertrekgegevens (NS-key/stationscode ingesteld?)",
            modifier = modifier,
        )

        else -> Unit
    }
}
