package com.healthconnect.app.data.remote

import com.healthconnect.app.data.remote.dto.ChatRequestDto
import com.healthconnect.app.data.remote.dto.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiService {
    @POST("chat")
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequestDto
    ): ChatResponseDto
}
