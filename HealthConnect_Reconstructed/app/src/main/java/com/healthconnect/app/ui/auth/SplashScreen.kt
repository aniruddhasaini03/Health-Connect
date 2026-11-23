package com.healthconnect.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome:()->Unit,onNavigateToLogin:()->Unit,vm:AuthViewModel=viewModel()){
    LaunchedEffect(Unit){delay(900);if(vm.isAuthenticated())onNavigateToHome() else onNavigateToLogin()}
    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Icon(Icons.Default.Favorite,null,Modifier.size(70.dp),tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.height(16.dp));Text("HealthConnect",style=MaterialTheme.typography.headlineMedium);Text("Your intelligent health companion",style=MaterialTheme.typography.bodyMedium)}}
}
