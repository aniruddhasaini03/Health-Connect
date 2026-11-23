package com.healthconnect.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.HealthMetricValue
import com.healthconnect.app.data.model.MetricType
import com.healthconnect.app.data.model.User
import com.healthconnect.app.data.repository.HealthDataRepository
import com.healthconnect.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(val isLoadingProfile:Boolean=false,val user:User?=null,val metrics:List<HealthMetricValue> = emptyList(),val isHealthDataConnected:Boolean=false,val errorMessage:String?=null)
class HomeViewModel(app:Application):AndroidViewModel(app){
    private val healthRepo=HealthDataRepository(app); private val userRepo=UserRepository()
    private val _uiState=MutableStateFlow(HomeUiState()); val uiState:StateFlow<HomeUiState> = _uiState.asStateFlow()
    fun loadDashboard()=viewModelScope.launch{
        val uid=FirebaseModule.auth.currentUser?.uid
        _uiState.value=_uiState.value.copy(isLoadingProfile=true)
        val user=uid?.let{userRepo.getProfile(it).getOrNull()}
        val available=runCatching{healthRepo.checkAvailability()}.getOrNull()
        val metrics=if(available?.hasAllPermissions==true) healthRepo.getTodayMetrics().getOrDefault(defaultDisconnectedMetrics()) else defaultDisconnectedMetrics()
        _uiState.value=HomeUiState(user=user,metrics=metrics,isHealthDataConnected=available?.hasAllPermissions==true)
    }
    private fun defaultDisconnectedMetrics()=listOf(
        HealthMetricValue(MetricType.STEPS,null,"steps",false), HealthMetricValue(MetricType.HEART_RATE,null,"bpm",false),
        HealthMetricValue(MetricType.SLEEP,null,"hrs",false), HealthMetricValue(MetricType.WATER,null,"L",false)
    )
}
