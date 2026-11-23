package com.healthconnect.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.ui.components.AppTextField
import com.healthconnect.app.ui.components.PrimaryButton
import com.healthconnect.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(onRegisterSuccess:()->Unit,onNavigateBack:()->Unit,vm:AuthViewModel=viewModel()){
 var name by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var pass by remember{mutableStateOf("")};var confirm by remember{mutableStateOf("")};val s=vm.registerState
 LaunchedEffect(s.isSuccess){if(s.isSuccess)onRegisterSuccess()}
 Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.Center){
  Text("Create your account",style=MaterialTheme.typography.headlineMedium);Text("Start your journey to better health");Spacer(Modifier.height(20.dp));AppTextField(name,{name=it},"Full Name");Spacer(Modifier.height(8.dp));AppTextField(email,{email=it},"Email");Spacer(Modifier.height(8.dp));OutlinedTextField(pass,{pass=it},label={Text("Password")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth());Spacer(Modifier.height(8.dp));OutlinedTextField(confirm,{confirm=it},label={Text("Confirm Password")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth());s.errorMessage?.let{Text(it,color=MaterialTheme.colorScheme.error)};Spacer(Modifier.height(12.dp));PrimaryButton(if(s.isLoading)"Creating..." else "Create Account",{vm.register(name,email,pass,confirm)},!s.isLoading);Row{Text("Already have an account?");TextButton(onClick=onNavigateBack){Text("Log in")}}
 }
}
