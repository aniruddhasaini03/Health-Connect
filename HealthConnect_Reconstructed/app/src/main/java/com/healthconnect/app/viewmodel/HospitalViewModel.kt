package com.healthconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.model.Hospital
import com.healthconnect.app.data.repository.HospitalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HospitalUiState(val isLoading:Boolean=false,val hospitals:List<Hospital> = emptyList(),val error:String?=null,val isLiveSearchConfigured:Boolean=true)
class HospitalViewModel(private val repo:HospitalRepository=HospitalRepository()):ViewModel(){
    private val _uiState=MutableStateFlow(HospitalUiState()); val uiState:StateFlow<HospitalUiState> = _uiState.asStateFlow()
    fun findNearby(lat:Double, lon:Double, radius:Int=10000)=viewModelScope.launch{_uiState.value=HospitalUiState(isLoading=true);_uiState.value=repo.getNearbyHospitals(lat,lon,radius).fold({HospitalUiState(hospitals=it)},{HospitalUiState(error=it.message?:"Failed to find nearby facilities")})}
}
