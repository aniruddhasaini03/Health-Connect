package com.healthconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.model.MedicalReport
import com.healthconnect.app.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ReportsUiState{ data object Loading:ReportsUiState; data class Success(val reports:List<MedicalReport>):ReportsUiState; data class Error(val message:String):ReportsUiState }
class ReportsViewModel(private val repo:ReportRepository=ReportRepository()):ViewModel(){
    private val _uiState=MutableStateFlow<ReportsUiState>(ReportsUiState.Loading);val uiState:StateFlow<ReportsUiState> = _uiState.asStateFlow()
    fun loadReports(){viewModelScope.launch{repo.getReports().collect{_uiState.value=it.fold({r->ReportsUiState.Success(r)},{e->ReportsUiState.Error(e.message?:"Failed to load reports")})}}}
    fun addReport(report:MedicalReport)=viewModelScope.launch{repo.addReport(report).onFailure{_uiState.value=ReportsUiState.Error(it.message?:"Failed to add report")}}
    fun deleteReport(id:String)=viewModelScope.launch{repo.deleteReport(id).onFailure{_uiState.value=ReportsUiState.Error(it.message?:"Failed to delete report")}}
}
