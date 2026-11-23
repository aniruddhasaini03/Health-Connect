package com.healthconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.HealthInsight
import com.healthconnect.app.data.model.InsightKind
import com.healthconnect.app.data.repository.GoalRepository
import com.healthconnect.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class InsightsUiState(val isLoading:Boolean=false,val insights:List<HealthInsight> = emptyList(),val errorMessage:String?=null)
class InsightsViewModel(private val users:UserRepository=UserRepository(),private val goals:GoalRepository=GoalRepository()):ViewModel(){
 private val _uiState=MutableStateFlow(InsightsUiState());val uiState:StateFlow<InsightsUiState> = _uiState.asStateFlow()
 fun loadInsights(){val uid=FirebaseModule.auth.currentUser?.uid?:return;viewModelScope.launch{
  _uiState.value=InsightsUiState(isLoading=true)
  val user=users.getProfile(uid).getOrNull();val gs=goals.getGoals(uid).getOrDefault(emptyList());val out=mutableListOf<HealthInsight>()
  out+=HealthInsight("info-source",InsightKind.INFORMATION,"About these insights","These observations are generated from simple patterns in your profile and goals. They are not a medical diagnosis. Connect the AI Health Assistant for deeper, personalized guidance.")
  if(user?.heightCm!=null&&user.weightKg!=null&&user.heightCm>0){val bmi=user.weightKg/((user.heightCm/100)*(user.heightCm/100));val band=when{bmi<18.5->"below the typical range";bmi<25->"within the typical range";bmi<30->"above the typical range";else->"well above the typical range"};out+=HealthInsight("bmi-insight",InsightKind.INSIGHT,"Body Mass Index","Your BMI is approximately %.1f, which is $band. BMI is a general screening measure, not a diagnosis — talk to a healthcare professional for a full picture.".format(bmi))}
  if(gs.isEmpty()) out+=HealthInsight("no-goals",InsightKind.RECOMMENDATION,"Set a health goal","You haven't set any health goals yet. Small, specific goals like daily water intake or step count can help build healthy habits.")
  val done=gs.count{it.isCompleted};if(done>0)out+=HealthInsight("completed-goals",InsightKind.INSIGHT,"Nice progress","You've completed $done goal(s). Keep up the consistency.")
  _uiState.value=InsightsUiState(insights=out)
 }}
}
