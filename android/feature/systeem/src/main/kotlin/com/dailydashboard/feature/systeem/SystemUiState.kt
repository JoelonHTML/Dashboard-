package com.dailydashboard.feature.systeem

sealed interface SystemUiState {
    data object Loading : SystemUiState
    data class Success(val stats: SystemStats) : SystemUiState
    data object Unavailable : SystemUiState
}
