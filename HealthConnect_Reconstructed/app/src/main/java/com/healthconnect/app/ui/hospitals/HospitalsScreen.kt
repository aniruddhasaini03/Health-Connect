package com.healthconnect.app.ui.hospitals

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.healthconnect.app.ui.components.EmptyState
import com.healthconnect.app.viewmodel.HospitalViewModel

@Composable
fun HospitalsScreen(onNavigateBack:()->Unit,vm:HospitalViewModel=viewModel()){
 val context=LocalContext.current;val s by vm.uiState.collectAsStateWithLifecycle();val client=remember{LocationServices.getFusedLocationProviderClient(context)}
 fun locate(){if(ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){client.lastLocation.addOnSuccessListener{loc->if(loc!=null)vm.findNearby(loc.latitude,loc.longitude) else vm.findNearby(28.6139,77.2090)}}}
 val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){granted->if(granted.values.any{it})locate()}
 LaunchedEffect(Unit){if(ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)locate()}
 Scaffold(topBar={TopAppBar(title={Text("Nearby Hospitals & Clinics")},navigationIcon={TextButton(onClick=onNavigateBack){Text("Back")}},actions={TextButton(onClick={locate()}){Text("Refresh")}})}){p->LazyColumn(Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  val permitted=ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
  if(!permitted)item{Card{Column(Modifier.padding(16.dp)){Text("Location Permission Needed",style=MaterialTheme.typography.titleMedium);Text("To find hospitals, clinics, and emergency centers near you, please grant location access.");Button(onClick={launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))}){Text("Grant Location Access")}}}}
  if(s.isLoading)item{Text("Searching nearby medical facilities...");LinearProgressIndicator(Modifier.fillMaxWidth())}
  s.error?.let{item{Card{Column(Modifier.padding(16.dp)){Text("Unable to load hospitals",style=MaterialTheme.typography.titleMedium);Text(it);Button(onClick={locate()}){Text("Try Again")}}}}}
  if(!s.isLoading&&permitted&&s.hospitals.isEmpty()&&s.error==null)item{EmptyState("No medical facilities found within 10 km.","Try refreshing or expanding the search later.")}
  items(s.hospitals,key={it.id}){h->Card{Column(Modifier.padding(14.dp)){Text(h.name,style=MaterialTheme.typography.titleMedium);Text(h.address);Text("%.1f km".format(h.distanceKm));Text(h.phoneNumber);Row{OutlinedButton(onClick={context.startActivity(Intent(Intent.ACTION_DIAL,Uri.parse("tel:${h.phoneNumber}")))},enabled=h.phoneNumber!="Not provided"){Text("Call")};Spacer(Modifier.width(8.dp));OutlinedButton(onClick={val uri=Uri.parse("google.navigation:q=${h.latitude},${h.longitude}");val g=Intent(Intent.ACTION_VIEW,uri).setPackage("com.google.android.apps.maps");runCatching{context.startActivity(g)}.getOrElse{context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${h.latitude},${h.longitude}")))}}){Text("Directions")}}}}}
 }}
}
