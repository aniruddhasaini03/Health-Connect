package com.healthconnect.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.ui.components.AppTextField
import com.healthconnect.app.ui.components.PrimaryButton
import com.healthconnect.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(onLoginSuccess:()->Unit,onNavigateToRegister:()->Unit,vm:AuthViewModel=viewModel()){
 var email by remember{mutableStateOf("")};var password by remember{mutableStateOf("")};var forgot by remember{mutableStateOf(false)}
 val s=vm.loginState
 LaunchedEffect(s.isSuccess){if(s.isSuccess)onLoginSuccess()}
 Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.Center){
  Text("Welcome back",style=MaterialTheme.typography.headlineMedium);Text("Sign in to continue to HealthConnect");Spacer(Modifier.height(24.dp))
  AppTextField(email,{email=it},"Email");Spacer(Modifier.height(10.dp))
  OutlinedTextField(password,{password=it},label={Text("Password")},visualTransformation=PasswordVisualTransformation(),singleLine=true,modifier=Modifier.fillMaxWidth())
  TextButton(onClick={forgot=true},modifier=Modifier.align(Alignment.End)){Text("Forgot password?")}
  s.errorMessage?.let{Text(it,color=MaterialTheme.colorScheme.error)}
  PrimaryButton(if(s.isLoading)"Signing in..." else "Log In",{vm.login(email,password)},!s.isLoading)
  Row(Modifier.align(Alignment.CenterHorizontally),verticalAlignment=Alignment.CenterVertically){Text("Don't have an account?");TextButton(onClick=onNavigateToRegister){Text("Create one")}}
 }
 if(forgot) ForgotPasswordDialog(onDismiss={forgot=false},vm=vm)
}

@Composable
private fun ForgotPasswordDialog(onDismiss:()->Unit,vm:AuthViewModel){var email by remember{mutableStateOf("")};val s=vm.resetPasswordState
 AlertDialog(onDismissRequest=onDismiss,title={Text("Reset your password")},text={Column{Text("Enter your email and we'll send you a link to reset your password.");Spacer(Modifier.height(8.dp));AppTextField(email,{email=it},"Email");s.errorMessage?.let{Text(it,color=MaterialTheme.colorScheme.error)};if(s.isSuccess)Text("Password reset email sent. Check your inbox.",color=MaterialTheme.colorScheme.primary)}},confirmButton={TextButton(onClick={vm.sendPasswordReset(email)}){Text(if(s.isLoading)"Sending..." else "Send Link")}},dismissButton={TextButton(onClick={vm.consumeResetState();onDismiss()}){Text("Cancel")}})
}
