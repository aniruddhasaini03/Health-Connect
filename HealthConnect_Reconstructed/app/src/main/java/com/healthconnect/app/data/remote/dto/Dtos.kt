package com.healthconnect.app.data.remote.dto

data class ChatMessageDto(val role: String = "user", val content: String = "")
data class UserHealthContextDto(
    val age: Int? = null,
    val gender: String = "",
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val healthGoals: List<String> = emptyList(),
    val recentReportSummaries: List<String> = emptyList()
)
data class ChatRequestDto(
    val messages: List<ChatMessageDto> = emptyList(),
    val userContext: UserHealthContextDto? = null
)
data class ChatResponseDto(val reply: String = "", val isUrgentWarning: Boolean = false)

data class PlaceLocationDto(val lat: Double = 0.0, val lng: Double = 0.0)
data class PlaceGeometryDto(val location: PlaceLocationDto = PlaceLocationDto())
data class PlaceResultDto(
    val name: String = "",
    val place_id: String = "",
    val geometry: PlaceGeometryDto = PlaceGeometryDto(),
    val vicinity: String = ""
)
data class PlacesSearchResponseDto(val results: List<PlaceResultDto> = emptyList(), val status: String = "")
