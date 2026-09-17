package com.dailydashboard.feature.ns

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

/**
 * NS-vertrektijden veranderen niet elke seconde en de bron is extern —
 * daarom altijd minstens 30s tussen syncs, ongeacht de algemene ververssnelheid.
 */
private const val MIN_POLL_INTERVAL_SECONDS = 30

class NsViewModel(
    private val repository: NsRepository,
    private val preferences: DashboardPreferencesDataSource,
) : ViewModel() {

    val uiState: StateFlow<NsUiState> = repository.observeDepartures()
        .map { departures -> NsUiState.Success(departures) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NsUiState.Loading,
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
                NsViewModel(
                    repository = OfflineFirstNsRepository.create(context.applicationContext),
                    preferences = DashboardPreferencesDataSource(context.applicationContext),
                )
            }
        }
    }
}
