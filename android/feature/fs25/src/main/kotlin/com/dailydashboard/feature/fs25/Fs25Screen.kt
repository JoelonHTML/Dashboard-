package com.dailydashboard.feature.fs25

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.ui.widgets.CalloutCard
import com.dailydashboard.core.ui.widgets.ChartCard
import com.dailydashboard.core.ui.widgets.ListCard
import com.dailydashboard.core.ui.widgets.ListRowData
import com.dailydashboard.core.ui.widgets.StatCard

@Composable
fun Fs25WidgetContent(
    dataSource: DataSource,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: Fs25ViewModel = viewModel(factory = Fs25ViewModel.factory(context))
    val uiState by viewModel.uiState.collectAsState()

    val stats = (uiState as? Fs25UiState.Success)?.stats

    when (dataSource) {
        DataSource.FS25_PLAYER_COUNT -> StatCard(
            label = if (stats?.online == true) "Spelers online" else "FS25 offline",
            value = stats?.playerCount?.toFloat() ?: 0f,
            modifier = modifier,
        )

        DataSource.FS25_ACTIVITY -> ChartCard(
            title = "FS25-activiteit",
            values = stats?.activityHistory ?: emptyList(),
            modifier = modifier,
        )

        DataSource.FS25_PLAYERS -> ListCard(
            title = stats?.mapName?.let { "Spelers — $it" } ?: "Spelers",
            items = stats?.playerNames?.mapIndexed { index, name ->
                ListRowData(marker = "${index + 1}", primaryText = name)
            } ?: emptyList(),
            emptyText = if (stats?.online == false) "Server offline" else "Geen spelers online",
            modifier = modifier,
        )

        DataSource.FS25_MAP -> CalloutCard(
            title = stats?.mapName ?: "Onbekende map",
            subtitle = if (stats?.online == true) {
                "${stats.serverName ?: "FS25-server"} · online"
            } else {
                "FS25-server offline"
            },
            modifier = modifier,
        )

        else -> Unit
    }
}
