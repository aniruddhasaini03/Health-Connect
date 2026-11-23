package com.healthconnect.app.ui.health

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.viewmodel.HealthDataViewModel

@Composable
fun HealthDataScreen(onNavigateBack:()->Unit,vm:HealthDataViewModel=viewModel()){
 val s by vm.uiState.collectAsStateWithLifecycle();val launcher=rememberLauncherForActivityResult(vm.repository.getPermissionRequestContract()){vm.refresh()};LaunchedEffect(Unit){vm.refresh()}
 Scaffold(topBar={TopAppBar(title={Text("Health Connect Vitals")},navigationIcon={TextButton(onClick=onNavigateBack){Text("Back")}},actions={TextButton(onClick={vm.refresh()}){Text("Sync Vitals")}})}){p->LazyColumn(Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  if(!s.availability.isSdkAvailable)item{Card{Column(Modifier.padding(16.dp)){Text("Health Connect Unavailable",style=MaterialTheme.typography.titleMedium);Text("Please ensure Health Connect is installed or enabled from Google Play Store on your device.")}}}
  else if(!s.availability.hasAllPermissions)item{Card{Column(Modifier.padding(16.dp)){Text("Connect Device Vitals",style=MaterialTheme.typography.titleMedium);Text("Allow Health Connect to sync your daily steps, resting heart rate, sleep duration, and hydration.");Spacer(Modifier.height(12.dp));Button(onClick={launcher.launch(com.healthconnect.app.data.repository.HealthDataRepository.REQUIRED_PERMISSIONS)}){Text("Grant Health Permissions")}}}}
  else{item{Text("Today's Aggregated Vitals",style=MaterialTheme.typography.titleLarge)};items(s.metrics){m->Card{Column(Modifier.padding(14.dp)){Text(m.type.name.replace('_',' '));Text("${m.value?.let{"%.1f".format(it)}?:"--"} ${m.unit}",style=MaterialTheme.typography.titleLarge)}}}}
  s.errorMessage?.let{item{Text(it,color=MaterialTheme.colorScheme.error)}}
 }}
}
