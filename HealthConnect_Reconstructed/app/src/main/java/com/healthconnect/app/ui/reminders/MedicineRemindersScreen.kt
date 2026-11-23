package com.healthconnect.app.ui.reminders

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.healthconnect.app.data.model.MedicineReminder
import com.healthconnect.app.ui.components.EmptyState
import com.healthconnect.app.utils.MedicineScheduler

@Composable
fun MedicineRemindersScreen(onNavigateBack:()->Unit){
 val context=LocalContext.current;var reminders by remember{mutableStateOf(load(context))};var add by remember{mutableStateOf(false)}
 val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){}
 LaunchedEffect(Unit){if(Build.VERSION.SDK_INT>=33&&ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)permission.launch(Manifest.permission.POST_NOTIFICATIONS)}
 fun persist(list:List<MedicineReminder>){reminders=list;save(context,list)}
 Scaffold(topBar={TopAppBar(title={Text("Medicine Daily Alarms")},navigationIcon={TextButton(onClick=onNavigateBack){Text("Back")}})},floatingActionButton={FloatingActionButton(onClick={add=true}){Text("+")}}){p->LazyColumn(Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){if(reminders.isEmpty())item{EmptyState("No Alarms Scheduled","Tap + to set a daily repeating alarm with custom tones and interactive clock.")}else items(reminders,key={it.id}){r->Card{Column(Modifier.padding(14.dp)){Text(r.medicineName,style=MaterialTheme.typography.titleMedium);Text("Dosage: ${r.dosage}");Text("Daily: %02d:%02d".format(r.hour,r.minute));Text("Tone: ${r.ringtoneTitle}");Row{Switch(checked=r.isEnabled,onCheckedChange={enabled->val n=reminders.map{if(it.id==r.id)it.copy(isEnabled=enabled)else it};persist(n);if(enabled)MedicineScheduler.scheduleDailyReminder(context,r.id,r.medicineName,r.dosage,r.hour,r.minute,r.ringtoneUri)else MedicineScheduler.cancelReminder(context,r.id)});TextButton(onClick={MedicineScheduler.cancelReminder(context,r.id);persist(reminders.filterNot{it.id==r.id})}){Text("Delete")}}}}}}}
 if(add)AddReminderDialog({add=false}){r->persist(reminders+r);MedicineScheduler.scheduleDailyReminder(context,r.id,r.medicineName,r.dosage,r.hour,r.minute,r.ringtoneUri);add=false}
}

@Composable private fun AddReminderDialog(onDismiss:()->Unit,onAdd:(MedicineReminder)->Unit){var name by remember{mutableStateOf("")};var dose by remember{mutableStateOf("")};var hour by remember{mutableStateOf("9")};var minute by remember{mutableStateOf("0")};var toneUri by remember{mutableStateOf("")};var toneTitle by remember{mutableStateOf("Default Alarm Ringtone")};val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){u:Uri?->if(u!=null){toneUri=u.toString();toneTitle="Custom Audio Track"}}
 AlertDialog(onDismissRequest=onDismiss,title={Text("Schedule Daily Alarm")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(name,{name=it},label={Text("Medicine Name")});OutlinedTextField(dose,{dose=it},label={Text("Dosage (e.g. 500mg, 1 tablet)")});Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(hour,{hour=it},label={Text("Hour")},modifier=Modifier.weight(1f));OutlinedTextField(minute,{minute=it},label={Text("Minute")},modifier=Modifier.weight(1f))};OutlinedButton(onClick={picker.launch("audio/*")}){Text("Alarm Tone: $toneTitle")}}},confirmButton={TextButton(onClick={onAdd(MedicineReminder(id=(System.currentTimeMillis()%Int.MAX_VALUE).toInt(),medicineName=name,dosage=dose,hour=hour.toIntOrNull()?.coerceIn(0,23)?:9,minute=minute.toIntOrNull()?.coerceIn(0,59)?:0,ringtoneUri=toneUri,ringtoneTitle=toneTitle))}){Text("Set Daily Alarm")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancel")}})
}
private fun load(c:Context):List<MedicineReminder>{val raw=c.getSharedPreferences("medicine_reminders",Context.MODE_PRIVATE).getString("items","[]")?:"[]";return runCatching{Gson().fromJson<List<MedicineReminder>>(raw,object:TypeToken<List<MedicineReminder>>(){}.type)}.getOrDefault(emptyList())}
private fun save(c:Context,list:List<MedicineReminder>){c.getSharedPreferences("medicine_reminders",Context.MODE_PRIVATE).edit().putString("items",Gson().toJson(list)).apply()}
