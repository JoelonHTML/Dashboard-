package com.dailydashboard.feature.herinneringen

sealed interface ReminderUiState {
    data object Loading : ReminderUiState
    data class Success(val reminders: List<ReminderItem>) : ReminderUiState
}
