package com.healthconnect.app.utils

import com.healthconnect.app.data.model.ConsultationUrgency
import com.healthconnect.app.data.model.LabParameter
import com.healthconnect.app.data.model.ParameterStatus

data class DetailedReportAnalysis(
    val overallStatus: String,
    val verdictSummary: String,
    val urgency: ConsultationUrgency,
    val parameters: List<LabParameter>,
    val lifestyleTips: List<String>,
    val doctorQuestions: List<String>
)

object ReportAnalysisEngine {
    fun analyzeComprehensive(rawText: String): DetailedReportAnalysis {
        val parameters = extractBiomarkers(rawText)
        return DetailedReportAnalysis(
            overallStatus = if (parameters.isEmpty()) "Text extracted" else "Basic local analysis complete",
            verdictSummary = "This offline fallback identifies common values only. Use the AI analysis or a clinician for interpretation.",
            urgency = ConsultationUrgency.ROUTINE,
            parameters = parameters,
            lifestyleTips = listOf("Stay hydrated", "Follow your clinician's advice", "Keep a copy of the original report"),
            doctorQuestions = listOf("Which values are most important for me?", "Do I need a repeat test?")
        )
    }

    fun extractBiomarkers(text: String): List<LabParameter> {
        val patterns = listOf("Hemoglobin", "HbA1c", "Glucose", "Creatinine", "Cholesterol", "TSH", "Vitamin D")
        return patterns.mapNotNull { name ->
            val r = Regex("(?i)$name\\s*[:=-]?\\s*([0-9]+(?:\\.[0-9]+)?)\\s*([a-zA-Z/%µ]+)?")
            val m = r.find(text) ?: return@mapNotNull null
            LabParameter(name = name, value = m.groupValues[1], unit = m.groupValues.getOrNull(2).orEmpty(), status = ParameterStatus.NORMAL)
        }
    }

    fun buildComprehensiveAiPrompt(rawText: String): String = "Analyze this medical report in plain English:\n$rawText"
}
