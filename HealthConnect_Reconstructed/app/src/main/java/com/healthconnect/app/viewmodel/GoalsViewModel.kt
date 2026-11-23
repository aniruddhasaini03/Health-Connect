package com.healthconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.HealthGoal
import com.healthconnect.app.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GoalsUiState(val isLoading: Boolean = false, val goals: List<HealthGoal> = emptyList(), val errorMessage: String? = null)
class GoalsViewModel(private val goalRepository: GoalRepository = GoalRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(GoalsUiState()); val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()
    private fun uid() = FirebaseModule.auth.currentUser?.uid
    fun loadGoals() { val u=uid() ?: return; viewModelScope.launch { _uiState.value=GoalsUiState(isLoading=true); _uiState.value=goalRepository.getGoals(u).fold({GoalsUiState(goals=it)},{GoalsUiState(errorMessage=it.message)}) } }
    fun createGoal(goal: HealthGoal) { val u=uid() ?: return; viewModelScope.launch { goalRepository.createGoal(u,goal); loadGoals() } }
    fun updateProgress(goal: HealthGoal, value: Double) { val u=uid() ?: return; viewModelScope.launch { val updated=goal.copy(currentValue=value,isCompleted=value>=goal.targetValue); goalRepository.updateGoal(u,updated); loadGoals() } }
    fun deleteGoal(goalId: String) { val u=uid() ?: return; viewModelScope.launch { goalRepository.deleteGoal(u,goalId); loadGoals() } }
}
