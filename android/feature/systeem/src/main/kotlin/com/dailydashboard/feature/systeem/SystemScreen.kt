package com.dailydashboard.feature.systeem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.ui.widgets.ChartCard
import com.dailydashboard.core.ui.widgets.GaugeCard
import com.dailydashboard.core.ui.widgets.StatCard

/** Rendert de widget-inhoud voor een systeem-databron; de kaart-vorm komt uit core:ui. */
@Composable
fun SystemWidgetContent(
    dataSource: DataSource,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: SystemViewModel = viewModel(factory = SystemViewModel.factory(context))
    val uiState by viewModel.uiState.collectAsState()

    val stats = (uiState as? SystemUiState.Success)?.stats

    when (dataSource) {
        DataSource.CPU_LOAD -> StatCard(
            label = "CPU",
            value = stats?.cpuLoadPercent ?: 0f,
            unit = "%",
            modifier = modifier,
        )

        DataSource.RAM_USAGE -> StatCard(
            label = "RAM",
            value = stats?.ramUsedPercent ?: 0f,
            unit = "%",
            modifier = modifier,
        )

        DataSource.DISK_USAGE -> StatCard(
            label = "Schijfruimte",
            value = stats?.diskUsedPercent ?: 0f,
            unit = "%",
            modifier = modifier,
        )

        DataSource.CPU_HISTORY -> ChartCard(
            title = "CPU-historie",
            values = stats?.cpuHistory ?: emptyList(),
            modifier = modifier,
        )

        DataSource.SERVER_LOAD -> GaugeCard(
            title = "Serverbelasting",
            progress = (stats?.cpuLoadPercent ?: 0f) / 100f,
            modifier = modifier,
        )

        else -> Unit
    }
}
