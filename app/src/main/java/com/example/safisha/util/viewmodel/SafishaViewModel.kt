package com.example.safisha.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.safisha.data.local.SafishaDatabase
import com.example.safisha.data.local.entity.IssueReportEntity
import com.example.safisha.data.local.entity.UserEntity
import com.example.safisha.data.repository.SafishaRepository
import com.example.safisha.data.repository.WeatherRepository
import com.example.safisha.domain.model.ReportStatus
import com.example.safisha.domain.model.UserRole
import com.example.safisha.util.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SafishaViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel only builds the repository; it never calls DAO methods.
    private val repository = SafishaRepository(
        SafishaDatabase.getDatabase(application).safishaDao()
    )
    private val weatherRepository = WeatherRepository()

    // ---------- Reports list state ----------
    private val filter = MutableStateFlow<ReportFilter>(ReportFilter.All)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reportsState: StateFlow<ReportsUiState> = filter
        .flatMapLatest { f ->
            val source: Flow<List<IssueReportEntity>> = when (f) {
                is ReportFilter.All -> repository.getAllReports()
                is ReportFilter.ByUser -> repository.getReportsByUser(f.userId)
                is ReportFilter.ByStatus -> repository.getReportsByStatus(f.status)
            }
            source
                .map { ReportsUiState(isLoading = false, reports = it) }
                .onStart { emit(ReportsUiState(isLoading = true)) }
                .catch { emit(ReportsUiState(isLoading = false, errorMessage = "Could not load reports")) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReportsUiState())

    fun setFilter(newFilter: ReportFilter) { filter.value = newFilter }

    // ---------- One-off events (Toasts, navigation) ----------
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()
    private fun send(event: UiEvent) { _events.trySend(event) }
    private fun say(text: String) = send(UiEvent.Message(text))

    // ----------For Current user (kept in memory) ----------
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    fun register(fullName: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            try {
                val cleanEmail = email.trim().lowercase()
                if (repository.getUserByEmail(cleanEmail) != null) {
                    say("An account with this email already exists")
                    return@launch
                }
                val hash = withContext(Dispatchers.Default) { PasswordHasher.hash(password) }
                repository.registerUser(
                    UserEntity(
                        fullName = fullName.trim(),
                        email = cleanEmail,
                        passwordHash = hash,
                        role = role
                    )
                )
                say("Account created. Please log in.")
                send(UiEvent.Registered)
            } catch (e: Exception) {
                say("Could not create the account")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserByEmail(email.trim().lowercase())
                val ok = user != null &&
                        withContext(Dispatchers.Default) { PasswordHasher.verify(password, user.passwordHash) }
                if (ok) {
                    _currentUser.value = user
                    send(UiEvent.LoggedIn)
                } else {
                    say("Wrong email or password")
                }
            } catch (e: Exception) {
                say("Could not log in")
            }
        }
    }

    fun logout() { _currentUser.value = null }

    // ---------- Report actions (business rules live here) ----------
    fun submitReport(categoryId: String, location: String, description: String) {
        val user = _currentUser.value
        if (user == null) { say("Please log in first"); return }
        viewModelScope.launch {
            try {
                repository.insertReport(
                    IssueReportEntity(
                        categoryId = categoryId,
                        location = location.trim(),
                        description = description.trim(),
                        reporterName = user.fullName,
                        reporterId = user.id
                    )   // status defaults to Pending, timestamp to now
                )
                say("Report submitted")
            } catch (e: Exception) {
                say("Could not save the report")
            }
        }
    }

    // Pending -> In Progress -> Resolved, administrators only
    fun advanceStatus(report: IssueReportEntity) {
        if (_currentUser.value?.role != UserRole.ADMIN) {
            say("Only administrators can update status"); return
        }
        val next = when (report.status) {
            ReportStatus.PENDING -> ReportStatus.IN_PROGRESS
            ReportStatus.IN_PROGRESS -> ReportStatus.RESOLVED
            else -> { say("This report is already resolved"); return }
        }
        viewModelScope.launch {
            try {
                val rows = repository.updateReportStatus(report.id, next)
                say(if (rows > 0) "Status updated to $next" else "Report not found")
            } catch (e: Exception) {
                say("Could not update the status")
            }
        }
    }

    fun deleteReport(report: IssueReportEntity) {
        if (_currentUser.value?.role != UserRole.ADMIN) {
            say("Only administrators can delete reports"); return
        }
        viewModelScope.launch {
            try {
                repository.deleteReport(report)
                say("Report deleted")
            } catch (e: Exception) {
                say("Could not delete the report")
            }
        }
    }

    // ---------- The Weather (external API) ----------
    private val _weather = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weather.asStateFlow()

    fun loadWeather() {
        viewModelScope.launch {
            _weather.value = WeatherUiState.Loading
            _weather.value = weatherRepository.fetchCurrentWeather().fold(
                onSuccess = { WeatherUiState.Success(it) },
                onFailure = {
                    WeatherUiState.Error(
                        if (it is IOException) "No internet connection. Weather unavailable."
                        else "Weather service is unavailable right now."
                    )
                }
            )
        }
    }
}