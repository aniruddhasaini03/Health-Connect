package com.healthconnect.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.model.HealthConnectAvailability
import com.healthconnect.app.data.model.HealthMetricValue
import com.healthconnect.app.data.repository.HealthDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HealthDataUiState(val isLoading:Boolean=false,val availability:HealthConnectAvailability=HealthConnectAvailability(),val metrics:List<HealthMetricValue> = emptyList(),val errorMessage:String?=null)
class HealthDataViewModel(app:Application):AndroidViewModel(app){
    val repository=HealthDataRepository(app)
    private val _uiState=MutableStateFlow(HealthDataUiState()); val uiState:StateFlow<HealthDataUiState> = _uiState.asStateFlow()
    fun refresh()=viewModelScope.launch{
        _uiState.value=_uiState.value.copy(isLoading=true,errorMessage=null)
        val availability=runCatching { repository.checkAvailability() }.getOrDefault(HealthConnectAvailability())
        if(!availability.isSdkAvailable){_uiState.value=HealthDataUiState(availability=availability,errorMessage="Health Connect is not installed on this device.");return@launch}
        if(!availability.hasAllPermissions){_uiState.value=HealthDataUiState(availability=availability);return@launch}
        _uiState.value=repository.getTodayMetrics().fold({HealthDataUiState(availability=availability,metrics=it)},{HealthDataUiState(availability=availability,errorMessage=it.message)})
    }
}
