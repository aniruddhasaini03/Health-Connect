package com.healthconnect.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class OverpassResponse(val elements: List<OverpassElement> = emptyList())
data class OverpassElement(
    val id: Long = 0L,
    val lat: Double? = null,
    val lon: Double? = null,
    val tags: Map<String, String> = emptyMap()
)

interface OsmHospitalService {
    @GET("interpreter")
    suspend fun findNearbyHospitals(@Query("data") query: String): OverpassResponse
}
