package com.dailydashboard.feature.weer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dailydashboard.core.datastore.DashboardPreferencesDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Het weer verandert niet elke seconde — minimaal 5 minuten tussen syncs. */
private const val MIN_POLL_INTERVAL_SECONDS = 300

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val preferences: DashboardPreferencesDataSource,
) : ViewModel() {

    val uiState: StateFlow<WeatherUiState> = repository.observeWeather()
        .map { weather -> if (weather == null) WeatherUiState.Loading else WeatherUiState.Success(weather) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeatherUiState.Loading,
        )

    init {
        viewModelScope.launch {
            preferences.settings.map { it.pollIntervalSeconds }.collectLatest { intervalSeconds ->
                val effectiveInterval = maxOf(intervalSeconds, MIN_POLL_INTERVAL_SECONDS)
                while (true) {
                    repository.sync()
                    delay(effectiveInterval * 1000L)
                }
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WeatherViewModel(
                    repository = OfflineFirstWeatherRepository.create(context.applicationContext),
                    preferences = DashboardPreferencesDataSource(context.applicationContext),
                )
            }
        }
    }
}
