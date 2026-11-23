package com.healthconnect.app.data.remote

import com.healthconnect.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    private val headerInterceptor = okhttp3.Interceptor { chain ->
        val req = chain.request().newBuilder()
            .header("User-Agent", "HealthConnectApp/1.0 (Android; Medical Aid Assistant)")
            .header("Accept", "application/json")
            .build()
        chain.proceed(req)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(logging)
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val aiService: AiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.AI_BACKEND_BASE_URL.ensureSlash())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(AiService::class.java)
    }

    val hospitalService: OsmHospitalService by lazy {
        Retrofit.Builder()
            .baseUrl("https://overpass-api.de/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(OsmHospitalService::class.java)
    }

    private fun String.ensureSlash() = if (endsWith('/')) this else "$this/"
}
