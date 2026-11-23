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
import com.healthconnect.app.viewmodel.InsightsViewModel

@Composable
fun InsightsScreen(onNavigateBack:()->Unit,vm:InsightsViewModel=viewModel()){
 val s by vm.uiState.collectAsStateWithLifecycle();LaunchedEffect(Unit){vm.loadInsights()};Scaffold(topBar={TopAppBar(title={Text("Health Insights")},navigationIcon={TextButton(onClick=onNavigateBack){Text("Back")}})}){p->LazyColumn(Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){items(s.insights){i->Card{Column(Modifier.padding(14.dp)){Text(i.kind.name.replace('_',' '),style=MaterialTheme.typography.labelMedium);Text(i.title,style=MaterialTheme.typography.titleMedium);Text(i.description)}}};s.errorMessage?.let{item{Text(it,color=MaterialTheme.colorScheme.error)}}}}
}
