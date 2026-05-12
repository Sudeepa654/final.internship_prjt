package com.example.suryashaktimain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suryashaktimain.data.local.EnergyLogEntity
import com.example.suryashaktimain.data.local.UserEntity
import com.example.suryashaktimain.data.repository.SolarRepository
import com.example.suryashaktimain.domain.DashboardMetrics
import com.example.suryashaktimain.domain.EnergyCalculator
import com.example.suryashaktimain.domain.ReportMetrics
import com.example.suryashaktimain.domain.WeatherCondition
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class EnergyUiState(
    val isSaving: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

class EnergyViewModel(
    private val repository: SolarRepository
) : ViewModel() {
    private val activeUser = MutableStateFlow<UserEntity?>(null)
    private val _logs = MutableStateFlow<List<EnergyLogEntity>>(emptyList())
    private val _uiState = MutableStateFlow(EnergyUiState())
    private var logJob: Job? = null

    val uiState: StateFlow<EnergyUiState> = _uiState.asStateFlow()
    val logs: StateFlow<List<EnergyLogEntity>> = _logs.asStateFlow()

    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(activeUser, _logs) { user, logs ->
        EnergyCalculator.buildDashboard(user, logs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardMetrics())

    val reportMetrics: StateFlow<ReportMetrics> = _logs.combine(activeUser) { logs, _ ->
        EnergyCalculator.buildReport(EnergyCalculator.logsWithinLast30Days(logs))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReportMetrics())

    fun setActiveUser(user: UserEntity?) {
        if (activeUser.value?.id == user?.id) return

        activeUser.value = user
        logJob?.cancel()
        _logs.value = emptyList()

        if (user != null) {
            logJob = viewModelScope.launch {
                repository.logsForUser(user.id).collect { userLogs ->
                    _logs.value = userLogs
                }
            }
        }
    }

    fun simulateGeneration(weather: WeatherCondition, capacityKw: Double): String {
        return EnergyCalculator.simulateGeneration(weather, capacityKw).toString()
    }

    fun simulateLogInputs(weather: WeatherCondition, capacityKw: Double) =
        EnergyCalculator.simulateLogInputs(
            weather = weather,
            capacityKw = capacityKw,
            lastMeterReading = _logs.value.firstOrNull()?.currentMeterReading
        )

    fun calculatePreview(
        solarGenerationText: String,
        unitsConsumedText: String,
        electricityRate: Double
    ) = EnergyCalculator.calculateLog(
        solarGeneration = solarGenerationText.toDoubleOrNull() ?: 0.0,
        unitsConsumed = unitsConsumedText.toDoubleOrNull() ?: 0.0,
        electricityRate = electricityRate
    )

    fun addLog(
        date: String,
        weatherCondition: WeatherCondition,
        solarGenerationText: String,
        batteryLevel: Int,
        previousMeterReadingText: String,
        currentMeterReadingText: String,
        unitsConsumedText: String
    ) {
        val user = activeUser.value
        val solarGeneration = solarGenerationText.toDoubleOrNull()
        val previousReading = previousMeterReadingText.toDoubleOrNull() ?: 0.0
        val currentReading = currentMeterReadingText.toDoubleOrNull() ?: 0.0
        val manualUnits = unitsConsumedText.toDoubleOrNull()
        val meterUnits = (currentReading - previousReading).coerceAtLeast(0.0)
        val unitsConsumed = manualUnits ?: meterUnits

        when {
            user == null -> showError("Please login before adding logs.")
            date.isBlank() -> showError("Please enter a date.")
            runCatching { LocalDate.parse(date) }.isFailure -> showError("Date format must be yyyy-MM-dd.")
            solarGeneration == null || solarGeneration < 0.0 -> showError("Solar generation must be 0 or more.")
            currentReading < previousReading -> showError("Current meter reading cannot be less than previous reading.")
            unitsConsumed < 0.0 -> showError("Units consumed must be 0 or more.")
            else -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isSaving = true, message = null, errorMessage = null) }
                    repository.addEnergyLog(
                        user = user,
                        date = date,
                        weatherCondition = weatherCondition.label,
                        solarGeneration = solarGeneration,
                        batteryLevel = batteryLevel,
                        previousMeterReading = previousReading,
                        currentMeterReading = currentReading,
                        unitsConsumed = unitsConsumed
                    ).onSuccess {
                        _uiState.update {
                            it.copy(isSaving = false, message = "Energy log saved.", errorMessage = null)
                        }
                    }.onFailure { throwable ->
                        showError(throwable.message ?: "Could not save energy log.")
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(message = null, errorMessage = null) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(isSaving = false, message = null, errorMessage = message) }
    }
}

