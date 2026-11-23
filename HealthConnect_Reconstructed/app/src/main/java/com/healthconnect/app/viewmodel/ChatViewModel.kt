package com.healthconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.ChatMessage
import com.healthconnect.app.data.model.MessageSender
import com.healthconnect.app.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(val isLoadingHistory:Boolean=false,val isSending:Boolean=false,val messages:List<ChatMessage> = emptyList(),val errorMessage:String?=null)
class ChatViewModel(private val repo:ChatRepository=ChatRepository()):ViewModel(){
    private val _uiState=MutableStateFlow(ChatUiState()); val uiState:StateFlow<ChatUiState> = _uiState.asStateFlow()
    private val urgentKeywords=listOf("chest pain","can't breathe","cannot breathe","severe bleeding","unconscious","suicide","overdose","stroke","heart attack","difficulty breathing")
    private fun uid()=FirebaseModule.auth.currentUser?.uid
    fun loadHistory(){val u=uid()?:return;viewModelScope.launch{_uiState.value=_uiState.value.copy(isLoadingHistory=true);_uiState.value=repo.getHistory(u).fold({ChatUiState(messages=it)},{ChatUiState(errorMessage=it.message)})}}
    fun sendMessage(text:String){if(text.isBlank())return;val u=uid()?:return;viewModelScope.launch{
        val current=_uiState.value.messages
        val localUser=ChatMessage(sender=MessageSender.USER,text=text)
        _uiState.value=_uiState.value.copy(messages=current+localUser,isSending=true,errorMessage=null)
        if(urgentKeywords.any{text.lowercase().contains(it)}){
            val warning=ChatMessage(sender=MessageSender.AI,text="This sounds like it could be a medical emergency. Please call your local emergency number or go to the nearest emergency room right away. I'm not able to provide emergency medical care.",isUrgentWarning=true)
            _uiState.value=_uiState.value.copy(messages=current+localUser+warning,isSending=false);return@launch
        }
        _uiState.value=repo.sendMessage(u,current,text).fold({ _uiState.value.copy(messages=current+localUser+it,isSending=false)},{_uiState.value.copy(isSending=false,errorMessage=it.message)})
    }}
    fun clearConversation(){val u=uid()?:return;viewModelScope.launch{repo.clearHistory(u);_uiState.value=ChatUiState()}}
    fun dismissError(){_uiState.value=_uiState.value.copy(errorMessage=null)}
}
