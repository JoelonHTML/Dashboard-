package com.dailydashboard.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dailydashboard.core.datastore.model.WidgetConfig
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

    private fun defaultLayout(): List<WidgetConfig> = emptyList()
}
