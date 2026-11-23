package com.healthconnect.app.data.api

import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.healthconnect.app.BuildConfig
import com.healthconnect.app.data.model.ConsultationUrgency
import com.healthconnect.app.data.model.LabParameter

/**
 * APK reconstruction note: the original app used gemini-1.5-flash directly from Android.
 * Do NOT hard-code a real API key in source; place GEMINI_API_KEY in local.properties.
 */
data class AiDetailedAnalysisResult(
    val overallStatus: String = "Analysis Complete",
    val verdictSummary: String = "Detailed analysis generated from medical report.",
    val consultationUrgency: ConsultationUrgency = ConsultationUrgency.ROUTINE,
    val parameters: List<LabParameter> = emptyList(),
    val lifestyleTips: List<String> = emptyList(),
    val doctorQuestions: List<String> = emptyList()
)

object ReportAiService {
    private val gson = Gson()
    private val generativeModel by lazy {
        require(BuildConfig.GEMINI_API_KEY.isNotBlank()) { "GEMINI_API_KEY is missing in local.properties" }
        GenerativeModel(modelName = "gemini-1.5-flash", apiKey = BuildConfig.GEMINI_API_KEY)
    }

    suspend fun analyzeReportWithGemini(rawText: String): Result<AiDetailedAnalysisResult> = runCatching {
        val prompt = """
            You are an expert, empathetic medical report interpreter for patients.
            Analyze the raw medical report text below and return ONLY valid JSON with this schema:
            {
              "overallStatus": "short plain-English status",
              "verdictSummary": "clear patient-friendly summary",
              "consultationUrgency": "ROUTINE|RECOMMENDED_SOON|URGENT|EMERGENCY",
              "parameters": [
                {
                  "name": "marker name",
                  "value": "value",
                  "unit": "unit",
                  "referenceRange": "range",
                  "status": "NORMAL|LOW|HIGH|ATTENTION|CRITICAL",
                  "simpleExplanation": "what this marker means",
                  "howToImprove": "specific practical food/lifestyle advice"
                }
              ],
              "lifestyleTips": ["specific practical tip"],
              "doctorQuestions": ["question the patient can ask a doctor"]
            }
            Never claim a diagnosis. For dangerous findings, recommend urgent professional care.

            RAW REPORT:
            $rawText
        """.trimIndent()
        val text = generativeModel.generateContent(prompt).text ?: error("Gemini returned no text")
        parseGeminiJson(text)
    }

    private fun parseGeminiJson(text: String): AiDetailedAnalysisResult {
        val clean = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        return gson.fromJson(clean, AiDetailedAnalysisResult::class.java)
    }
}
