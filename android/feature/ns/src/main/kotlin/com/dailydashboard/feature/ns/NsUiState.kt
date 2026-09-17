package com.dailydashboard.feature.ns

sealed interface NsUiState {
    data object Loading : NsUiState
    data class Success(val departures: List<NsDeparture>) : NsUiState
}
