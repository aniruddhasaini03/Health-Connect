package com.healthconnect.app.data.model

enum class MessageSender { USER, AI }
data class ChatMessage(
    val id: String = "",
    val sender: MessageSender = MessageSender.USER,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isUrgentWarning: Boolean = false
)

data class EmergencyContact(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val relationship: String = ""
)

enum class FacilityType { HOSPITAL, CLINIC, PHARMACY }
data class HealthFacility(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phoneNumber: String = "",
    val distanceMeters: Double? = null,
    val type: FacilityType = FacilityType.HOSPITAL
)

enum class GoalCategory { STEPS, WATER, EXERCISE, SLEEP, WELLNESS }
data class HealthGoal(
    val id: String = "",
    val title: String = "",
    val targetValue: Double = 0.0,
    val currentValue: Double = 0.0,
    val unit: String = "",
    val category: GoalCategory = GoalCategory.WELLNESS,
    val createdAt: Long = System.currentTimeMillis(),
    val targetDate: Long? = null,
    val isCompleted: Boolean = false
)

enum class InsightKind { INFORMATION, INSIGHT, RECOMMENDATION, EMERGENCY_WARNING }
data class HealthInsight(
    val id: String = "",
    val kind: InsightKind = InsightKind.INFORMATION,
    val title: String = "",
    val description: String = "",
    val generatedAt: Long = System.currentTimeMillis()
)

enum class MetricType { STEPS, HEART_RATE, SLEEP, WATER }
data class HealthMetricValue(
    val type: MetricType = MetricType.STEPS,
    val value: Double? = null,
    val unit: String = "",
    val isConnected: Boolean = false,
    val lastUpdated: Long? = null
)
data class HealthConnectAvailability(
    val isSdkAvailable: Boolean = false,
    val hasAllPermissions: Boolean = false
)

data class Hospital(
    val id: String = "",
    val name: String = "Healthcare Facility",
    val address: String = "Nearby Location",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phoneNumber: String = "Not provided",
    val rating: Double = 0.0,
    val isOpenNow: Boolean = false,
    val distanceKm: Double = 0.0
)

enum class ParameterStatus { NORMAL, LOW, HIGH, ATTENTION, CRITICAL }
enum class ConsultationUrgency { ROUTINE, RECOMMENDED_SOON, URGENT, EMERGENCY }
enum class ReportType { LAB_RESULT, PRESCRIPTION, IMAGING, DISCHARGE_SUMMARY, OTHER }

data class LabParameter(
    val name: String = "Biomarker",
    val value: String = "--",
    val unit: String = "",
    val referenceRange: String = "N/A",
    val status: ParameterStatus = ParameterStatus.NORMAL,
    val simpleExplanation: String = "",
    val howToImprove: String = ""
)

data class MedicalReport(
    val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val type: ReportType = ReportType.OTHER,
    val mimeType: String = "image/jpeg",
    val sizeBytes: Long = 0L,
    val localUriString: String = "",
    val addedAt: Long = System.currentTimeMillis(),
    val rawExtractedText: String = "",
    val overallHealthStatus: String = "",
    val overallVerdictSummary: String = "",
    val consultationUrgency: ConsultationUrgency = ConsultationUrgency.ROUTINE,
    val keyParameters: List<LabParameter> = emptyList(),
    val dietAndLifestyleTips: List<String> = emptyList(),
    val doctorQuestions: List<String> = emptyList()
)

data class MedicineReminder(
    val id: Int = 0,
    val medicineName: String = "",
    val dosage: String = "",
    val hour: Int = 9,
    val minute: Int = 0,
    val isEnabled: Boolean = true,
    val ringtoneUri: String = "",
    val ringtoneTitle: String = "Default Alarm Ringtone"
)

enum class TimelineEventType { REPORT_ADDED, GOAL_COMPLETED, APPOINTMENT, AI_INSIGHT, PROFILE_UPDATED, OTHER }
data class TimelineEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: TimelineEventType = TimelineEventType.OTHER
)

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val age: Int? = null,
    val gender: String = "",
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val healthGoals: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
