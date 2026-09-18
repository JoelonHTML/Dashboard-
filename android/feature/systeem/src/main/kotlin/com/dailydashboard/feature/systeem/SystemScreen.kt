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
            unit = "%",
            modifier = modifier,
        )

        DataSource.SERVER_LOAD -> GaugeCard(
            title = "Serverbelasting",
            progress = (stats?.cpuLoadPercent ?: 0f) / 100f,
            modifier = modifier,
        )

        DataSource.RAM_GAUGE -> GaugeCard(
            title = "Geheugengebruik",
            progress = (stats?.ramUsedPercent ?: 0f) / 100f,
            modifier = modifier,
        )

        DataSource.DISK_GAUGE -> GaugeCard(
            title = "Schijfgebruik",
            progress = (stats?.diskUsedPercent ?: 0f) / 100f,
            modifier = modifier,
        )

        DataSource.NETWORK_DOWN -> StatCard(
            label = "Download",
            value = stats?.networkDownKBps ?: 0f,
            unit = " KB/s",
            valueFormat = { "%.1f".format(it) },
            modifier = modifier,
        )

        DataSource.NETWORK_UP -> StatCard(
            label = "Upload",
            value = stats?.networkUpKBps ?: 0f,
            unit = " KB/s",
            valueFormat = { "%.1f".format(it) },
            modifier = modifier,
        )

        DataSource.UPTIME_HOURS -> StatCard(
            label = "Uptime laptop",
            value = stats?.uptimeHours ?: 0f,
            unit = " u",
            modifier = modifier,
        )

        DataSource.LAPTOP_BATTERY_PERCENT -> StatCard(
            label = if (stats?.batteryCharging == true) "Batterij (opladen)" else "Batterij laptop",
            value = stats?.batteryPercent?.toFloat() ?: 0f,
            unit = "%",
            modifier = modifier,
        )

        DataSource.LAPTOP_BATTERY_GAUGE -> GaugeCard(
            title = "Batterij laptop",
            progress = (stats?.batteryPercent?.toFloat() ?: 0f) / 100f,
            modifier = modifier,
        )

        DataSource.CPU_TEMP -> StatCard(
            label = "CPU-temperatuur",
            value = stats?.cpuTempCelsius ?: 0f,
            unit = "°C",
            modifier = modifier,
        )

        else -> Unit
    }
}
