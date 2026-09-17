package com.dailydashboard.feature.fs25

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

class Fs25ViewModel(
    private val repository: Fs25Repository,
    private val preferences: DashboardPreferencesDataSource,
) : ViewModel() {

    val uiState: StateFlow<Fs25UiState> = repository.observeFs25Stats()
        .map { stats -> if (stats == null) Fs25UiState.Loading else Fs25UiState.Success(stats) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Fs25UiState.Loading,
        )

    init {
        viewModelScope.launch {
            preferences.settings.map { it.pollIntervalSeconds }.collectLatest { intervalSeconds ->
                while (true) {
                    repository.sync()
                    delay(intervalSeconds * 1000L)
                }
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                Fs25ViewModel(
                    repository = OfflineFirstFs25Repository.create(context.applicationContext),
                    preferences = DashboardPreferencesDataSource(context.applicationContext),
                )
            }
        }
    }
}
