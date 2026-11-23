package com.healthconnect.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.ui.components.HealthCard
import com.healthconnect.app.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(onEditProfile:()->Unit,onNavigateToGoals:()->Unit,onNavigateToTimeline:()->Unit,onNavigateToInsights:()->Unit,onLogout:()->Unit,vm:ProfileViewModel=viewModel()){
 val s by vm.uiState.collectAsStateWithLifecycle();LaunchedEffect(Unit){vm.loadProfile()};val u=s.user
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{Text("Profile",style=MaterialTheme.typography.headlineSmall)}
  item{HealthCard{Text(u?.name?.ifBlank{"Your Name"}?:"Your Name",style=MaterialTheme.typography.titleLarge);Text(u?.email.orEmpty());OutlinedButton(onClick=onEditProfile){Text("Edit Profile")}}}
  item{HealthCard{Info("Age",u?.age?.toString()?:"Not set");Info("Gender",u?.gender?.ifBlank{"Not set"}?:"Not set");Info("Height",u?.heightCm?.let{"%.1f cm".format(it)}?:"Not set");Info("Weight",u?.weightKg?.let{"%.1f kg".format(it)}?:"Not set");val bmi=if(u?.heightCm!=null&&u.weightKg!=null&&u.heightCm>0)u.weightKg/((u.heightCm/100)*(u.heightCm/100)) else null;Info("BMI",bmi?.let{"%.1f".format(it)}?:"Not available");Info("Emergency Contact",listOfNotNull(u?.emergencyContactName?.takeIf{it.isNotBlank()},u?.emergencyContactPhone?.takeIf{it.isNotBlank()}).joinToString(" · ").ifBlank{"Not set"})}}
  item{NavButton("Health Goals",Icons.Default.Flag,onNavigateToGoals);NavButton("Health Timeline",Icons.Default.Timeline,onNavigateToTimeline);NavButton("Health Insights",Icons.Default.Lightbulb,onNavigateToInsights)}
  item{OutlinedButton(onClick=onLogout,modifier=Modifier.fillMaxWidth()){Text("Log Out")}}
 }
}
@Composable private fun Info(k:String,v:String){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(k);Text(v)}}
@Composable private fun NavButton(t:String,i:androidx.compose.ui.graphics.vector.ImageVector,on:()->Unit){OutlinedButton(onClick=on,modifier=Modifier.fillMaxWidth()){Icon(i,null);Spacer(Modifier.width(8.dp));Text(t)}}
