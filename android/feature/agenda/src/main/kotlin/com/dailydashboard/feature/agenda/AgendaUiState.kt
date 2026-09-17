package com.dailydashboard.feature.agenda

sealed interface AgendaUiState {
    data object Loading : AgendaUiState
    data class Success(val events: List<AgendaEvent>) : AgendaUiState
}
