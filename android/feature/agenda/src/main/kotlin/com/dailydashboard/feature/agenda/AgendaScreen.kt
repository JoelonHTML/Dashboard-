package com.dailydashboard.feature.agenda

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.ui.widgets.CalloutCard
import com.dailydashboard.core.ui.widgets.ListCard
import com.dailydashboard.core.ui.widgets.ListRowData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

@Composable
fun AgendaWidgetContent(
    dataSource: DataSource,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: AgendaViewModel = viewModel(factory = AgendaViewModel.factory(context))
    val uiState by viewModel.uiState.collectAsState()

    val events = (uiState as? AgendaUiState.Success)?.events ?: emptyList()
    val now = Instant.now()
    val today = now.atZone(ZoneId.systemDefault()).toLocalDate()

    when (dataSource) {
        DataSource.NEXT_APPOINTMENT -> {
            val next = events.filter { it.start.isAfter(now) }.minByOrNull { it.start }
            CalloutCard(
                title = next?.title ?: "Geen afspraken",
                subtitle = next?.let { "Volgende afspraak — ${timeFormatter.format(it.start)}" } ?: "Agenda is leeg",
                modifier = modifier,
            )
        }

        DataSource.AGENDA_TODAY -> {
            val todayEvents = events.filter { it.start.atZone(ZoneId.systemDefault()).toLocalDate() == today }
            ListCard(
                title = "Agenda vandaag",
                items = todayEvents.map { event ->
                    ListRowData(
                        marker = timeFormatter.format(event.start),
                        primaryText = event.title,
                        secondaryText = event.location,
                    )
                },
                emptyText = "Geen afspraken vandaag",
                modifier = modifier,
            )
        }

        else -> Unit
    }
}
