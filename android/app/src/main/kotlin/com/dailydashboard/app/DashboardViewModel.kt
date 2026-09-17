package com.dailydashboard.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dailydashboard.core.datastore.DashboardPreferencesDataSource
import com.dailydashboard.core.datastore.DashboardSettings
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.datastore.model.WidgetConfig
import com.dailydashboard.core.datastore.model.WidgetSize
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val widgets: List<WidgetConfig> = emptyList(),
    val settings: DashboardSettings = DashboardSettings(),
    val isNight: Boolean = false,
)

class DashboardViewModel(
    private val preferences: DashboardPreferencesDataSource,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        preferences.widgetLayout,
        preferences.settings,
        minuteTicker(),
    ) { widgets, settings, nowMinute ->
        DashboardUiState(
            widgets = widgets.sortedBy { it.position },
            settings = settings,
            isNight = computeIsNight(settings, nowMinute),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(),
    )

    fun addWidget(dataSource: DataSource, size: WidgetSize = WidgetSize.WIDE) {
        viewModelScope.launch {
            val current = uiState.value.widgets
            val newWidget = WidgetConfig(
                id = UUID.randomUUID().toString(),
                type = dataSource.widgetType,
                size = size,
                dataSource = dataSource,
                position = current.size,
            )
            preferences.saveWidgetLayout(current + newWidget)
        }
    }

    fun removeWidget(id: String) {
        viewModelScope.launch {
            val updated = uiState.value.widgets
                .filterNot { it.id == id }
                .mapIndexed { index, widget -> widget.copy(position = index) }
            preferences.saveWidgetLayout(updated)
        }
    }

    fun cycleWidgetSize(id: String) {
        viewModelScope.launch {
            val updated = uiState.value.widgets.map { widget ->
                if (widget.id == id) widget.copy(size = widget.size.next()) else widget
            }
            preferences.saveWidgetLayout(updated)
        }
    }

    fun reorder(newOrder: List<WidgetConfig>) {
        viewModelScope.launch {
            preferences.saveWidgetLayout(newOrder.mapIndexed { index, widget -> widget.copy(position = index) })
        }
    }

    fun updateBackendBaseUrl(url: String) = viewModelScope.launch { preferences.updateBackendBaseUrl(url) }

    fun updatePollIntervalSeconds(seconds: Int) = viewModelScope.launch { preferences.updatePollIntervalSeconds(seconds) }

    fun updateNightModeEnabled(enabled: Boolean) = viewModelScope.launch { preferences.updateNightModeEnabled(enabled) }

    fun updateNightWindow(startMinute: Int, endMinute: Int) =
        viewModelScope.launch { preferences.updateNightWindow(startMinute, endMinute) }

    fun setManualNightOverride(value: Boolean?) = viewModelScope.launch { preferences.setManualNightOverride(value) }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { DashboardViewModel(DashboardPreferencesDataSource(context.applicationContext)) }
        }
    }
}

private fun WidgetSize.next(): WidgetSize = when (this) {
    WidgetSize.SMALL -> WidgetSize.WIDE
    WidgetSize.WIDE -> WidgetSize.LARGE
    WidgetSize.LARGE -> WidgetSize.SMALL
}

/** Tikt elke minuut zodat nachtmodus vanzelf in-/uitschakelt zonder herstart van de app. */
private fun minuteTicker(): Flow<Int> = flow {
    while (true) {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        emit(now.hour * 60 + now.minute)
        delay(60_000L)
    }
}

internal fun computeIsNight(settings: DashboardSettings, nowMinuteOfDay: Int): Boolean {
    settings.manualNightOverride?.let { return it }
    if (!settings.nightModeEnabled) return false

    return if (settings.nightStartMinute <= settings.nightEndMinute) {
        nowMinuteOfDay in settings.nightStartMinute until settings.nightEndMinute
    } else {
        nowMinuteOfDay >= settings.nightStartMinute || nowMinuteOfDay < settings.nightEndMinute
    }
}
