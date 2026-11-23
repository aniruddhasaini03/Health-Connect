package com.healthconnect.app.data.remote

import com.healthconnect.app.data.remote.dto.PlacesSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** Optional Google Places client mirrored from classes present in the APK. */
interface PlacesService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun nearbySearch(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String = "hospital",
        @Query("key") apiKey: String
    ): PlacesSearchResponseDto
}
