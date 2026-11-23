package com.healthconnect.app.ui.emergency

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.data.model.EmergencyContact
import com.healthconnect.app.ui.components.EmptyState
import com.healthconnect.app.viewmodel.EmergencyViewModel

@Composable
fun EmergencyScreen(onNavigateToHospitals:()->Unit,onNavigateBack:()->Unit,vm:EmergencyViewModel=viewModel()){
 val context=LocalContext.current;val s by vm.uiState.collectAsStateWithLifecycle();var add by remember{mutableStateOf(false)};LaunchedEffect(Unit){vm.loadContacts()}
 Scaffold(topBar={TopAppBar(title={Text("Emergency")},navigationIcon={TextButton(onClick=onNavigateBack){Text("Back")}})},floatingActionButton={FloatingActionButton(onClick={add=true}){Text("+")}}){p->LazyColumn(Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.errorContainer)){Column(Modifier.padding(16.dp)){Icon(Icons.Default.Emergency,null);Text("Get help quickly. Nothing here is called automatically.",style=MaterialTheme.typography.titleMedium);Button(onClick={context.startActivity(Intent(Intent.ACTION_DIAL,Uri.parse("tel:112")))}){Text("Call Emergency Services")};Text("Opens your phone dialer — you confirm the call")}}}
  item{OutlinedButton(onClick=onNavigateToHospitals,modifier=Modifier.fillMaxWidth()){Text("Find Nearby Hospitals")};Text("Emergency Contacts",style=MaterialTheme.typography.titleLarge)}
  if(s.contacts.isEmpty())item{EmptyState("No emergency contacts added yet.","Add a trusted contact for quick access.")} else items(s.contacts,key={it.id}){c->Card{Column(Modifier.padding(14.dp)){Text(c.name,style=MaterialTheme.typography.titleMedium);Text("${c.relationship} · ${c.phoneNumber}");Row{TextButton(onClick={context.startActivity(Intent(Intent.ACTION_DIAL,Uri.parse("tel:${c.phoneNumber}")))}){Text("Call")};TextButton(onClick={vm.deleteContact(c.id)}){Text("Delete")}}}}}
 }}
 if(add)AddContactDialog({add=false}){vm.addContact(it);add=false}
}
@Composable private fun AddContactDialog(onDismiss:()->Unit,onAdd:(EmergencyContact)->Unit){var name by remember{mutableStateOf("")};var phone by remember{mutableStateOf("")};var rel by remember{mutableStateOf("")};AlertDialog(onDismissRequest=onDismiss,title={Text("Add Emergency Contact")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(name,{name=it},label={Text("Name")});OutlinedTextField(phone,{phone=it},label={Text("Phone Number")});OutlinedTextField(rel,{rel=it},label={Text("Relationship")})}},confirmButton={TextButton(onClick={onAdd(EmergencyContact(name=name,phoneNumber=phone,relationship=rel))}){Text("Add")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancel")}})}
