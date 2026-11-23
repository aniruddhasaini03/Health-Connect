package com.healthconnect.app.data.remote

import com.healthconnect.app.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Optional Google Places fallback. The reconstructed active hospital flow uses
 * OpenStreetMap/Overpass so the app still works without a Maps key.
 */
object PlacesRetrofitInstance {
    val service: PlacesService by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlacesService::class.java)
    }
    fun isConfigured(): Boolean = BuildConfig.MAPS_API_KEY.isNotBlank()
}
