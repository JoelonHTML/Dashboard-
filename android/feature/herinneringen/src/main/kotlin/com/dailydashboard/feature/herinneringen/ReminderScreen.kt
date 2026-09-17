package com.dailydashboard.feature.herinneringen

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
import com.dailydashboard.core.ui.widgets.StatCard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("d MMM").withZone(ZoneId.systemDefault())

@Composable
fun ReminderWidgetContent(
    dataSource: DataSource,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: ReminderViewModel = viewModel(factory = ReminderViewModel.factory(context))
    val uiState by viewModel.uiState.collectAsState()

    val reminders = (uiState as? ReminderUiState.Success)?.reminders ?: emptyList()
    val today = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()

    when (dataSource) {
        DataSource.TASKS_TODAY_COUNT -> {
            val countToday = reminders.count { it.deadline?.atZone(ZoneId.systemDefault())?.toLocalDate() == today }
            StatCard(label = "Taken vandaag", value = countToday.toFloat(), modifier = modifier)
        }

        DataSource.TOP_REMINDER -> {
            val top = reminders.filter { it.deadline != null }.minByOrNull { it.deadline!! } ?: reminders.firstOrNull()
            CalloutCard(
                title = top?.title ?: "Geen herinneringen",
                subtitle = top?.deadline?.let { "Deadline — ${dateFormatter.format(it)}" } ?: "Alles afgerond",
                modifier = modifier,
            )
        }

        DataSource.OPEN_REMINDERS -> {
            ListCard(
                title = "Openstaande herinneringen",
                items = reminders.map { reminder ->
                    ListRowData(
                        marker = "!",
                        primaryText = reminder.title,
                        secondaryText = reminder.list,
                        statusText = reminder.deadline?.let { dateFormatter.format(it) },
                        statusPositive = false,
                    )
                },
                emptyText = "Niets openstaand",
                modifier = modifier,
            )
        }

        else -> Unit
    }
}
