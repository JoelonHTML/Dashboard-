package com.dailydashboard.feature.fs25

sealed interface Fs25UiState {
    data object Loading : Fs25UiState
    data class Success(val stats: Fs25Stats) : Fs25UiState
}
