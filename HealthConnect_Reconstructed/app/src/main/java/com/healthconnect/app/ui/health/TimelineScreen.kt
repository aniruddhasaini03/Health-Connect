package com.healthconnect.app.ui.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.ui.components.EmptyState
import com.healthconnect.app.viewmodel.TimelineViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimelineScreen(onNavigateBack:()->Unit,vm:TimelineViewModel=viewModel()){
 val s by vm.uiState.collectAsStateWithLifecycle();LaunchedEffect(Unit){vm.loadTimeline()};Scaffold(topBar={TopAppBar(title={Text("Health Timeline")},navigationIcon={TextButton(onClick=onNavigateBack){Text("Back")}})}){p->LazyColumn(Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){if(s.events.isEmpty())item{EmptyState("Nothing here yet","Your health events, reports, and milestones will appear here over time.")}else items(s.events){e->Card{Column(Modifier.padding(14.dp)){Text(e.title,style=MaterialTheme.typography.titleMedium);Text(e.description);Text(SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.getDefault()).format(Date(e.timestamp)),style=MaterialTheme.typography.labelSmall)}}}}}
}
