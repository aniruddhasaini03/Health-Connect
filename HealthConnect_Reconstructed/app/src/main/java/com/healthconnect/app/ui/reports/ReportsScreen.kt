package com.healthconnect.app.ui.reports

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.data.api.ReportAiService
import com.healthconnect.app.data.model.*
import com.healthconnect.app.ui.components.EmptyState
import com.healthconnect.app.utils.ReportAnalysisEngine
import com.healthconnect.app.utils.ReportScanner
import com.healthconnect.app.viewmodel.ReportsUiState
import com.healthconnect.app.viewmodel.ReportsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsScreen(onNavigateBack:()->Unit,vm:ReportsViewModel=viewModel()){
    val state by vm.uiState.collectAsStateWithLifecycle(); val context= LocalContext.current; val scope= rememberCoroutineScope()
    var processing by remember{mutableStateOf<String?>(null)}; var selected by remember{mutableStateOf<MedicalReport?>(null)}
    LaunchedEffect(Unit){vm.loadReports()}
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){uri: Uri?->
        if(uri!=null)scope.launch{
            processing="Reading document text with on-device OCR..."
            val raw=ReportScanner.extractTextFromImageUri(context,uri).getOrElse{""}
            processing="Gemini AI is analyzing each biomarker & generating insights..."
            val ai=ReportAiService.analyzeReportWithGemini(raw).getOrElse{
                val f=ReportAnalysisEngine.analyzeComprehensive(raw)
                com.healthconnect.app.data.api.AiDetailedAnalysisResult(f.overallStatus,f.verdictSummary,f.urgency,f.parameters,f.lifestyleTips,f.doctorQuestions)
            }
            val report=MedicalReport(displayName="Report - ${SimpleDateFormat("dd MMM, hh:mm a",Locale.getDefault()).format(Date())}",localUriString=uri.toString(),rawExtractedText=raw,overallHealthStatus=ai.overallStatus,overallVerdictSummary=ai.verdictSummary,consultationUrgency=ai.consultationUrgency,keyParameters=ai.parameters,dietAndLifestyleTips=ai.lifestyleTips,doctorQuestions=ai.doctorQuestions)
            vm.addReport(report);processing=null
        }
    }
    Scaffold(topBar={TopAppBar(title={Text("Medical Reports Intelligence")},navigationIcon={TextButton(onClick=onNavigateBack){Text("Back")}},actions={TextButton(onClick={picker.launch("image/*")}){Text("Scan New Report")}})}){p->
        LazyColumn(Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
            processing?.let{msg->item{Card{Column(Modifier.padding(16.dp)){CircularProgressIndicator();Spacer(Modifier.height(8.dp));Text(msg)}}}}
            when(val s=state){
                ReportsUiState.Loading->item{CircularProgressIndicator()}
                is ReportsUiState.Error->item{Text(s.message,color=MaterialTheme.colorScheme.error)}
                is ReportsUiState.Success-> if(s.reports.isEmpty())item{EmptyState("No Medical Reports Uploaded","Tap Scan New Report to scan blood tests, urine tests, or doctor prescriptions for an AI breakdown.")}else items(s.reports,key={it.id}){r->ReportCard(r,{selected=r},{vm.deleteReport(r.id)})}
            }
        }
    }
    selected?.let{ReportDetailDialog(it){selected=null}}
}

@Composable private fun ReportCard(r:MedicalReport,onClick:()->Unit,onDelete:()->Unit){Card(onClick=onClick){Column(Modifier.padding(14.dp)){Text(r.displayName,style=MaterialTheme.typography.titleMedium);Text("Status: ${r.overallHealthStatus}");Text(urgencyLabel(r.consultationUrgency));Text(SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.getDefault()).format(Date(r.addedAt)),style=MaterialTheme.typography.labelSmall);TextButton(onClick=onDelete){Text("Delete Report")}}}}
private fun urgencyLabel(u:ConsultationUrgency)=when(u){ConsultationUrgency.EMERGENCY->"Immediate Medical Attention";ConsultationUrgency.URGENT->"Consult Within 24-48h";ConsultationUrgency.RECOMMENDED_SOON->"Consult Doctor Soon";ConsultationUrgency.ROUTINE->"Routine Checkup"}

@Composable private fun ReportDetailDialog(r:MedicalReport,onDismiss:()->Unit){AlertDialog(onDismissRequest=onDismiss,confirmButton={TextButton(onClick=onDismiss){Text("Done")}},title={Text(r.displayName)},text={LazyColumn(Modifier.heightIn(max=560.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){item{Text(r.overallVerdictSummary)};item{Text("Biomarkers & Values",style=MaterialTheme.typography.titleMedium)};items(r.keyParameters){p->Card{Column(Modifier.padding(10.dp)){Text("${p.name}: ${p.value} ${p.unit}");Text("Standard Ref: ${p.referenceRange}");Text("Meaning: ${p.simpleExplanation}");Text("How to improve: ${p.howToImprove}")}}};item{Text("What Needs Improvement & Action Steps",style=MaterialTheme.typography.titleMedium);r.dietAndLifestyleTips.forEach{Text("• $it")}};item{Text("Questions To Ask Your Doctor",style=MaterialTheme.typography.titleMedium);r.doctorQuestions.forEach{Text("• $it")}};item{Text("Extracted Text (Original Scanned)",style=MaterialTheme.typography.titleMedium);Text(r.rawExtractedText.ifBlank{"No text detected."})}}})}
