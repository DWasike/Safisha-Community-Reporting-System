package com.example.safisha.ui.viewmodel

import com.example.safisha.data.local.entity.IssueReportEntity
import com.example.safisha.data.repository.WeatherSummary

data class ReportsUiState(
    val isLoading: Boolean = true,
    val reports: List<IssueReportEntity> = emptyList(),
    val errorMessage: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && errorMessage == null && reports.isEmpty()
}

sealed interface ReportFilter {
    data object All : ReportFilter
    data class ByUser(val userId: Int) : ReportFilter
    data class ByStatus(val status: String) : ReportFilter
}

sealed interface UiEvent {
    data class Message(val text: String) : UiEvent
    data object LoggedIn : UiEvent
    data object Registered : UiEvent
}

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: WeatherSummary) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}