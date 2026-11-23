package com.healthconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.User
import com.healthconnect.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(val isLoading: Boolean = false, val isSaving: Boolean = false, val user: User? = null, val errorMessage: String? = null, val saveSuccess: Boolean = false)

class ProfileViewModel(private val userRepository: UserRepository = UserRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        val uid = FirebaseModule.auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _uiState.value = userRepository.getProfile(uid).fold(
                { ProfileUiState(user = it) }, { ProfileUiState(errorMessage = it.message) }
            )
        }
    }
    fun saveProfile(user: User) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isSaving = true)
        _uiState.value = userRepository.updateProfile(user).fold(
            { _uiState.value.copy(isSaving = false, user = user, saveSuccess = true) },
            { _uiState.value.copy(isSaving = false, errorMessage = it.message) }
        )
    }
    fun consumeSaveSuccess() { _uiState.value = _uiState.value.copy(saveSuccess = false) }
}
