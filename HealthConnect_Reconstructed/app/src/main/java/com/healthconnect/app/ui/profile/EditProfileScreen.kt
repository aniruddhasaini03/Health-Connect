package com.healthconnect.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.data.model.User
import com.healthconnect.app.ui.components.AppTextField
import com.healthconnect.app.ui.components.PrimaryButton
import com.healthconnect.app.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(onNavigateBack:()->Unit,vm:ProfileViewModel=viewModel()){
 val s by vm.uiState.collectAsStateWithLifecycle();LaunchedEffect(Unit){vm.loadProfile()};var loaded by remember{mutableStateOf(false)};var name by remember{mutableStateOf("")};var age by remember{mutableStateOf("")};var gender by remember{mutableStateOf("")};var height by remember{mutableStateOf("")};var weight by remember{mutableStateOf("")};var eName by remember{mutableStateOf("")};var ePhone by remember{mutableStateOf("")}
 LaunchedEffect(s.user){if(!loaded&&s.user!=null){val u=s.user!!;name=u.name;age=u.age?.toString().orEmpty();gender=u.gender;height=u.heightCm?.toString().orEmpty();weight=u.weightKg?.toString().orEmpty();eName=u.emergencyContactName;ePhone=u.emergencyContactPhone;loaded=true}}
 LaunchedEffect(s.saveSuccess){if(s.saveSuccess){vm.consumeSaveSuccess();onNavigateBack()}}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Row{TextButton(onClick=onNavigateBack){Text("Back")};Text("Edit Profile",style=MaterialTheme.typography.headlineSmall)};AppTextField(name,{name=it},"Full Name");AppTextField(age,{age=it},"Age");AppTextField(gender,{gender=it},"Gender");AppTextField(height,{height=it},"Height (cm)");AppTextField(weight,{weight=it},"Weight (kg)");Text("Emergency Contact",style=MaterialTheme.typography.titleMedium);AppTextField(eName,{eName=it},"Contact Name");AppTextField(ePhone,{ePhone=it},"Contact Phone");s.errorMessage?.let{Text(it,color=MaterialTheme.colorScheme.error)};PrimaryButton(if(s.isSaving)"Saving..." else "Save Profile",{val base=s.user?:return@PrimaryButton;vm.saveProfile(base.copy(name=name,age=age.toIntOrNull(),gender=gender,heightCm=height.toDoubleOrNull(),weightKg=weight.toDoubleOrNull(),emergencyContactName=eName,emergencyContactPhone=ePhone))},!s.isSaving)}
}
