package com.dailydashboard.feature.weer

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: WeatherState) : WeatherUiState
}
