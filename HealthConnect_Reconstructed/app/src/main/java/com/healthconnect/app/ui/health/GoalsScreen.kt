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
import com.healthconnect.app.data.model.GoalCategory
import com.healthconnect.app.data.model.HealthGoal
import com.healthconnect.app.ui.components.EmptyState
import com.healthconnect.app.viewmodel.GoalsViewModel

@Composable
fun GoalsScreen(onNavigateBack:()->Unit,vm:GoalsViewModel=viewModel()){
 val s by vm.uiState.collectAsStateWithLifecycle();var add by remember{mutableStateOf(false)};LaunchedEffect(Unit){vm.loadGoals()}
 Scaffold(topBar={TopAppBar(title={Text("Health Goals")},navigationIcon={TextButton(onClick=onNavigateBack){Text("Back")}})},floatingActionButton={FloatingActionButton(onClick={add=true}){Text("+")}}){p->LazyColumn(Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){if(s.goals.isEmpty())item{EmptyState("No goals yet","Tap the + button to set your first health goal.")}else items(s.goals,key={it.id}){g->Card{Column(Modifier.padding(14.dp)){Text(g.title,style=MaterialTheme.typography.titleMedium);Text("${g.currentValue} / ${g.targetValue} ${g.unit}");LinearProgressIndicator(progress={if(g.targetValue<=0)0f else (g.currentValue/g.targetValue).coerceIn(0.0,1.0).toFloat()},modifier=Modifier.fillMaxWidth());if(g.isCompleted)Text("Completed",color=MaterialTheme.colorScheme.primary);Row{TextButton(onClick={vm.updateProgress(g,(g.currentValue+g.targetValue*0.1).coerceAtMost(g.targetValue))}){Text("Add progress")};TextButton(onClick={vm.deleteGoal(g.id)}){Text("Delete")}}}}}}}
 if(add)AddGoalDialog({add=false}){vm.createGoal(it);add=false}
}

@Composable private fun AddGoalDialog(onDismiss:()->Unit,onCreate:(HealthGoal)->Unit){var title by remember{mutableStateOf("")};var target by remember{mutableStateOf("")};var unit by remember{mutableStateOf("")};var cat by remember{mutableStateOf(GoalCategory.WELLNESS)};var menu by remember{mutableStateOf(false)}
 AlertDialog(onDismissRequest=onDismiss,title={Text("New Health Goal")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(title,{title=it},label={Text("Goal title")});OutlinedTextField(target,{target=it},label={Text("Target value")});OutlinedTextField(unit,{unit=it},label={Text("Unit (e.g. steps, L, hrs)")});Box{OutlinedButton(onClick={menu=true}){Text("Category: ${cat.name}")};DropdownMenu(menu,{menu=false}){GoalCategory.entries.forEach{DropdownMenuItem(text={Text(it.name)},onClick={cat=it;menu=false})}}}}},confirmButton={TextButton(onClick={onCreate(HealthGoal(title=title,targetValue=target.toDoubleOrNull()?:0.0,unit=unit,category=cat))}){Text("Create")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancel")}})
}
