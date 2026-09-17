package com.dailydashboard.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dailydashboard.core.datastore.model.DataSource
import com.dailydashboard.core.datastore.model.WidgetConfig
import com.dailydashboard.core.datastore.model.WidgetSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dashboardDataStore by preferencesDataStore(name = "dashboard_settings")

private object Keys {
    val WIDGET_LAYOUT_JSON = stringPreferencesKey("widget_layout_json")
    val BACKEND_BASE_URL = stringPreferencesKey("backend_base_url")
    val POLL_INTERVAL_SECONDS = intPreferencesKey("poll_interval_seconds")
    val NIGHT_MODE_ENABLED = booleanPreferencesKey("night_mode_enabled")
    val NIGHT_START_MINUTE = intPreferencesKey("night_start_minute")
    val NIGHT_END_MINUTE = intPreferencesKey("night_end_minute")
    val MANUAL_NIGHT_OVERRIDE = booleanPreferencesKey("manual_night_override")
    val HAS_MANUAL_NIGHT_OVERRIDE = booleanPreferencesKey("has_manual_night_override")
    val ACCENT_COLOR_HEX = stringPreferencesKey("accent_color_hex")
    val GRID_COLUMNS = intPreferencesKey("grid_columns")
}

/** Source of truth voor widget-layout en instellingen — overleeft een herstart. */
class DashboardPreferencesDataSource(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val defaults = DashboardSettings()

    val settings: Flow<DashboardSettings> = context.dashboardDataStore.data.map { prefs ->
        DashboardSettings(
            backendBaseUrl = prefs[Keys.BACKEND_BASE_URL] ?: defaults.backendBaseUrl,
            pollIntervalSeconds = prefs[Keys.POLL_INTERVAL_SECONDS] ?: defaults.pollIntervalSeconds,
            nightModeEnabled = prefs[Keys.NIGHT_MODE_ENABLED] ?: defaults.nightModeEnabled,
            nightStartMinute = prefs[Keys.NIGHT_START_MINUTE] ?: defaults.nightStartMinute,
            nightEndMinute = prefs[Keys.NIGHT_END_MINUTE] ?: defaults.nightEndMinute,
            manualNightOverride = if (prefs[Keys.HAS_MANUAL_NIGHT_OVERRIDE] == true) {
                prefs[Keys.MANUAL_NIGHT_OVERRIDE]
            } else {
                null
            },
            accentColorHex = prefs[Keys.ACCENT_COLOR_HEX] ?: defaults.accentColorHex,
            gridColumns = prefs[Keys.GRID_COLUMNS] ?: defaults.gridColumns,
        )
    }

    val widgetLayout: Flow<List<WidgetConfig>> = context.dashboardDataStore.data.map { prefs ->
        val raw = prefs[Keys.WIDGET_LAYOUT_JSON] ?: return@map defaultLayout()
        runCatching { json.decodeFromString<List<WidgetConfig>>(raw) }.getOrElse { defaultLayout() }
    }

    suspend fun saveWidgetLayout(layout: List<WidgetConfig>) {
        context.dashboardDataStore.edit { prefs ->
            prefs[Keys.WIDGET_LAYOUT_JSON] = json.encodeToString(layout)
        }
    }

    suspend fun updateBackendBaseUrl(url: String) {
        context.dashboardDataStore.edit { prefs -> prefs[Keys.BACKEND_BASE_URL] = url }
    }

    suspend fun updatePollIntervalSeconds(seconds: Int) {
        context.dashboardDataStore.edit { prefs -> prefs[Keys.POLL_INTERVAL_SECONDS] = seconds }
    }

    suspend fun updateNightModeEnabled(enabled: Boolean) {
        context.dashboardDataStore.edit { prefs -> prefs[Keys.NIGHT_MODE_ENABLED] = enabled }
    }

    suspend fun updateNightWindow(startMinute: Int, endMinute: Int) {
        context.dashboardDataStore.edit { prefs ->
            prefs[Keys.NIGHT_START_MINUTE] = startMinute
            prefs[Keys.NIGHT_END_MINUTE] = endMinute
        }
    }

    suspend fun setManualNightOverride(value: Boolean?) {
        context.dashboardDataStore.edit { prefs ->
            if (value == null) {
                prefs[Keys.HAS_MANUAL_NIGHT_OVERRIDE] = false
            } else {
                prefs[Keys.HAS_MANUAL_NIGHT_OVERRIDE] = true
                prefs[Keys.MANUAL_NIGHT_OVERRIDE] = value
            }
        }
    }

    /** Zet de widget-grid terug naar de meegeleverde standaardindeling. */
    suspend fun resetWidgetLayoutToDefault() {
        saveWidgetLayout(defaultLayout())
    }

    /** [hex] als "#RRGGBB", of null om terug te vallen op de standaard-accentkleur van het thema. */
    suspend fun updateAccentColor(hex: String?) {
        context.dashboardDataStore.edit { prefs ->
            if (hex == null) prefs.remove(Keys.ACCENT_COLOR_HEX) else prefs[Keys.ACCENT_COLOR_HEX] = hex
        }
    }

    suspend fun updateGridColumns(columns: Int) {
        context.dashboardDataStore.edit { prefs -> prefs[Keys.GRID_COLUMNS] = columns.coerceIn(2, 6) }
    }
}

/**
 * Vult de grid bij een verse install alvast met een compleet, representatief dashboard
 * (alle vijf widget-types) in plaats van een lege grid — direct bruikbaar bij eerste opstart.
 */
private fun defaultWidget(id: String, size: WidgetSize, dataSource: DataSource, position: Int) = WidgetConfig(
    id = id,
    type = dataSource.widgetType,
    size = size,
    dataSource = dataSource,
    position = position,
)

private fun defaultLayout(): List<WidgetConfig> = listOf(
    defaultWidget("default-cpu", WidgetSize.SMALL, DataSource.CPU_LOAD, 0),
    defaultWidget("default-ram", WidgetSize.SMALL, DataSource.RAM_USAGE, 1),
    defaultWidget("default-disk", WidgetSize.SMALL, DataSource.DISK_USAGE, 2),
    defaultWidget("default-fs25-players", WidgetSize.SMALL, DataSource.FS25_PLAYER_COUNT, 3),
    defaultWidget("default-cpu-history", WidgetSize.WIDE, DataSource.CPU_HISTORY, 4),
    defaultWidget("default-server-load", WidgetSize.WIDE, DataSource.SERVER_LOAD, 5),
    defaultWidget("default-next-appointment", WidgetSize.WIDE, DataSource.NEXT_APPOINTMENT, 6),
    defaultWidget("default-agenda-today", WidgetSize.WIDE, DataSource.AGENDA_TODAY, 7),
    defaultWidget("default-open-reminders", WidgetSize.WIDE, DataSource.OPEN_REMINDERS, 8),
    defaultWidget("default-fs25-list", WidgetSize.WIDE, DataSource.FS25_PLAYERS, 9),
)
