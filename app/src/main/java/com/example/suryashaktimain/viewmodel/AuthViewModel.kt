package com.example.suryashaktimain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suryashaktimain.data.local.UserEntity
import com.example.suryashaktimain.data.repository.SolarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: UserEntity? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(
    private val repository: SolarRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            showError("Please enter email and password.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            repository.login(email, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            isLoading = false,
                            successMessage = "Welcome back, ${user.name}."
                        )
                    }
                }
                .onFailure { throwable ->
                    showError(throwable.message ?: "Login failed.")
                }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        homeLocation: String,
        solarCapacityText: String,
        electricityRateText: String
    ) {
        val capacity = solarCapacityText.toDoubleOrNull()
        val rate = electricityRateText.toDoubleOrNull()

        when {
            name.isBlank() -> showError("Please enter your name.")
            email.isBlank() -> showError("Please enter your email.")
            !email.contains("@") -> showError("Please enter a valid email address.")
            password.length < 4 -> showError("Password must be at least 4 characters.")
            homeLocation.isBlank() -> showError("Please enter home location.")
            capacity == null || capacity <= 0.0 -> showError("Solar capacity must be greater than 0.")
            rate == null || rate <= 0.0 -> showError("Electricity rate must be greater than 0.")
            else -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                    repository.registerUser(
                        name = name,
                        email = email,
                        password = password,
                        homeLocation = homeLocation,
                        solarPanelCapacityKw = capacity,
                        electricityUnitRate = rate
                    ).onSuccess { user ->
                        _uiState.update {
                            it.copy(
                                currentUser = user,
                                isLoading = false,
                                successMessage = "Account created successfully."
                            )
                        }
                    }.onFailure { throwable ->
                        showError(throwable.message ?: "Registration failed.")
                    }
                }
            }
        }
    }

    fun resetPassword(email: String, newPassword: String) {
        when {
            email.isBlank() -> showError("Please enter your registered email.")
            newPassword.length < 4 -> showError("New password must be at least 4 characters.")
            else -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                    repository.resetPassword(email, newPassword)
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    successMessage = "Password updated. Please login again."
                                )
                            }
                        }
                        .onFailure { throwable ->
                            showError(throwable.message ?: "Password reset failed.")
                        }
                }
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState(successMessage = "Logged out.")
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(isLoading = false, errorMessage = message, successMessage = null)
        }
    }
}

