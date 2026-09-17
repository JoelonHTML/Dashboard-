package com.dailydashboard.feature.weer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.ui.widgets.ListCard
import com.dailydashboard.core.ui.widgets.ListRowData
import com.dailydashboard.core.ui.widgets.StatCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val dayFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("nl"))

@Composable
fun WeatherWidgetContent(
    dataSource: DataSource,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: WeatherViewModel = viewModel(factory = WeatherViewModel.factory(context))
    val uiState by viewModel.uiState.collectAsState()

    val weather = (uiState as? WeatherUiState.Success)?.weather

    when (dataSource) {
        DataSource.WEATHER_TEMP -> StatCard(
            label = weather?.currentDescription ?: "Weer",
            value = weather?.currentTempCelsius ?: 0f,
            unit = "°C",
            modifier = modifier,
        )

        DataSource.WEATHER_FORECAST -> ListCard(
            title = "Weersverwachting",
            items = weather?.forecast?.map { day ->
                val label = runCatching { LocalDate.parse(day.date) }
                    .getOrNull()
                    ?.dayOfWeek
                    ?.getDisplayName(TextStyle.SHORT, Locale("nl"))
                    ?: day.date

                ListRowData(
                    marker = label.take(2).uppercase(),
                    primaryText = day.description,
                    secondaryText = runCatching { LocalDate.parse(day.date).format(dayFormatter) }.getOrNull(),
                    statusText = listOfNotNull(
                        day.maxTempCelsius?.let { "${it.toInt()}°" },
                        day.minTempCelsius?.let { "${it.toInt()}°" },
                    ).joinToString(" / ").ifBlank { null },
                )
            } ?: emptyList(),
            emptyText = "Geen weergegevens",
            modifier = modifier,
        )

        else -> Unit
    }
}
