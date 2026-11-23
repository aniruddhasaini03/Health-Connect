package com.healthconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.TimelineEvent
import com.healthconnect.app.data.repository.TimelineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TimelineUiState(val isLoading:Boolean=false,val events:List<TimelineEvent> = emptyList(),val errorMessage:String?=null)
class TimelineViewModel(private val repo:TimelineRepository=TimelineRepository()):ViewModel(){
    private val _uiState=MutableStateFlow(TimelineUiState()); val uiState:StateFlow<TimelineUiState> = _uiState.asStateFlow()
    fun loadTimeline(){ val uid=FirebaseModule.auth.currentUser?.uid?:return; viewModelScope.launch{ _uiState.value=TimelineUiState(isLoading=true); _uiState.value=repo.getEvents(uid).fold({TimelineUiState(events=it)},{TimelineUiState(errorMessage=it.message)}) } }
}
