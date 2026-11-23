package com.healthconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.EmergencyContact
import com.healthconnect.app.data.repository.EmergencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmergencyUiState(val isLoading:Boolean=false,val contacts:List<EmergencyContact> = emptyList(), val errorMessage:String?=null)
class EmergencyViewModel(private val emergencyRepository: EmergencyRepository = EmergencyRepository()):ViewModel(){
    private val _uiState=MutableStateFlow(EmergencyUiState()); val uiState:StateFlow<EmergencyUiState> = _uiState.asStateFlow()
    private fun uid()=FirebaseModule.auth.currentUser?.uid
    fun loadContacts(){ val u=uid()?:return; viewModelScope.launch{ _uiState.value=EmergencyUiState(isLoading=true); _uiState.value=emergencyRepository.getContacts(u).fold({EmergencyUiState(contacts=it)},{EmergencyUiState(errorMessage=it.message)}) } }
    fun addContact(contact:EmergencyContact){ val u=uid()?:return; viewModelScope.launch{ emergencyRepository.addContact(u,contact); loadContacts() } }
    fun deleteContact(id:String){ val u=uid()?:return; viewModelScope.launch{ emergencyRepository.deleteContact(u,id); loadContacts() } }
}
