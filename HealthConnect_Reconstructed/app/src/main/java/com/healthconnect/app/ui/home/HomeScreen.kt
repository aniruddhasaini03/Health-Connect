package com.healthconnect.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.data.model.MetricType
import com.healthconnect.app.ui.components.HealthCard
import com.healthconnect.app.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(onNavigateToReminders:()->Unit,onNavigateToReports:()->Unit,onNavigateToAi:()->Unit,onNavigateToHospitals:()->Unit,onNavigateToEmergency:()->Unit,onNavigateToHealthData:()->Unit,vm:HomeViewModel=viewModel()){
 val s by vm.uiState.collectAsStateWithLifecycle();LaunchedEffect(Unit){vm.loadDashboard()}
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{Text("Health Overview",style=MaterialTheme.typography.headlineSmall);Text(SimpleDateFormat("EEEE, dd MMMM",Locale.getDefault()).format(Date()));Text("Hi, ${s.user?.name?.ifBlank{"there"} ?: "there"}")}
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){s.metrics.take(2).forEach{MetricCard(it.type.name.replace('_',' '),it.value?.let{v->if(v%1.0==0.0)v.toInt().toString() else "%.1f".format(v)}?:"--",it.unit,Modifier.weight(1f))}}}
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){s.metrics.drop(2).take(2).forEach{MetricCard(it.type.name.replace('_',' '),it.value?.let{v->"%.1f".format(v)}?:"--",it.unit,Modifier.weight(1f))}}}
  if(!s.isHealthDataConnected)item{Button(onClick=onNavigateToHealthData,modifier=Modifier.fillMaxWidth()){Text("Connect Vitals")}}
  item{Text("Quick Actions",style=MaterialTheme.typography.titleLarge)}
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){Quick(Icons.Default.Alarm,"Medicine\nAlarms",onNavigateToReminders,Modifier.weight(1f));Quick(Icons.Default.DocumentScanner,"Scan Lab\nReport",onNavigateToReports,Modifier.weight(1f));Quick(Icons.Default.SmartToy,"AI Health\nAssistant",onNavigateToAi,Modifier.weight(1f))}}
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){Quick(Icons.Default.LocalHospital,"Nearby\nHospitals",onNavigateToHospitals,Modifier.weight(1f));Quick(Icons.Default.Emergency,"Emergency\nSOS",onNavigateToEmergency,Modifier.weight(1f));Spacer(Modifier.weight(1f))}}
  item{HealthCard{Text("Latest Lab Analysis",style=MaterialTheme.typography.titleMedium);Text("Open Medical Reports to scan blood tests or prescription slips.")}}
 }
}
@Composable private fun MetricCard(title:String,value:String,unit:String,modifier:Modifier){Card(modifier){Column(Modifier.padding(14.dp)){Text(title,style=MaterialTheme.typography.labelMedium);Text("$value $unit",style=MaterialTheme.typography.titleLarge)}}}
@Composable private fun Quick(icon:ImageVector,label:String,onClick:()->Unit,modifier:Modifier){OutlinedCard(onClick=onClick,modifier=modifier){Column(Modifier.padding(12.dp)){Icon(icon,null);Spacer(Modifier.height(6.dp));Text(label)}}}
