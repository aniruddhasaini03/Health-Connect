package com.healthconnect.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.repository.AuthRepository
import com.healthconnect.app.utils.Validators
import kotlinx.coroutines.launch

data class AuthUiState(val isLoading: Boolean = false, val errorMessage: String? = null, val isSuccess: Boolean = false)

class AuthViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {
    var loginState by mutableStateOf(AuthUiState()); private set
    var registerState by mutableStateOf(AuthUiState()); private set
    var resetPasswordState by mutableStateOf(AuthUiState()); private set

    fun isAuthenticated() = authRepository.isAuthenticated()
    fun logout() = authRepository.logout()

    fun login(email: String, password: String) {
        if (!Validators.isValidEmail(email)) { loginState = AuthUiState(errorMessage = "Please enter a valid email address."); return }
        if (password.isBlank()) { loginState = AuthUiState(errorMessage = "Password is required."); return }
        viewModelScope.launch {
            loginState = AuthUiState(isLoading = true)
            loginState = authRepository.login(email, password).fold(
                { AuthUiState(isSuccess = true) },
                { AuthUiState(errorMessage = it.message ?: "Login failed. Please try again.") }
            )
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        Validators.validationError(name, email, password, confirmPassword)?.let { registerState = AuthUiState(errorMessage = it); return }
        viewModelScope.launch {
            registerState = AuthUiState(isLoading = true)
            registerState = authRepository.register(name, email, password).fold(
                { AuthUiState(isSuccess = true) },
                { AuthUiState(errorMessage = it.message ?: "Registration failed. Please try again.") }
            )
        }
    }

    fun sendPasswordReset(email: String) {
        if (!Validators.isValidEmail(email)) { resetPasswordState = AuthUiState(errorMessage = "Please enter a valid email address."); return }
        viewModelScope.launch {
            resetPasswordState = AuthUiState(isLoading = true)
            resetPasswordState = authRepository.sendPasswordResetEmail(email).fold(
                { AuthUiState(isSuccess = true) }, { AuthUiState(errorMessage = it.message) }
            )
        }
    }
    fun clearLoginError() { loginState = loginState.copy(errorMessage = null) }
    fun clearRegisterError() { registerState = registerState.copy(errorMessage = null) }
    fun consumeResetState() { resetPasswordState = AuthUiState() }
}
