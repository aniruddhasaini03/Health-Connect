package com.healthconnect.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.ChatMessage
import com.healthconnect.app.data.model.MessageSender
import com.healthconnect.app.data.remote.RetrofitInstance
import com.healthconnect.app.data.remote.dto.ChatMessageDto
import com.healthconnect.app.data.remote.dto.ChatRequestDto
import com.healthconnect.app.data.remote.dto.UserHealthContextDto
import kotlinx.coroutines.tasks.await

class ChatRepository(
    private val userRepository: UserRepository = UserRepository(),
    private val firestore: FirebaseFirestore = FirebaseModule.firestore,
    private val auth: FirebaseAuth = FirebaseModule.auth
) {
    private fun chatCollection(uid: String) = firestore.collection("users").document(uid).collection("chatMessages")

    private suspend fun persistMessage(uid: String, message: ChatMessage) {
        val doc = chatCollection(uid).document(message.id.ifBlank { chatCollection(uid).document().id })
        doc.set(message.copy(id = doc.id)).await()
    }

    suspend fun getHistory(uid: String): Result<List<ChatMessage>> = runCatching {
        chatCollection(uid).orderBy("timestamp").get().await().documents
            .mapNotNull { it.toObject(ChatMessage::class.java) }
    }

    suspend fun clearHistory(uid: String): Result<Unit> = runCatching {
        val docs = chatCollection(uid).get().await().documents
        val batch = firestore.batch()
        docs.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    suspend fun sendMessage(uid: String, currentHistory: List<ChatMessage>, text: String): Result<ChatMessage> = runCatching {
        val userMessage = ChatMessage(sender = MessageSender.USER, text = text)
        persistMessage(uid, userMessage)

        val profile = userRepository.getProfile(uid).getOrNull()
        val token = auth.currentUser?.getIdToken(false)?.await()?.token.orEmpty()
        val request = ChatRequestDto(
            messages = (currentHistory + userMessage).takeLast(20).map {
                ChatMessageDto(if (it.sender == MessageSender.USER) "user" else "assistant", it.text)
            },
            userContext = UserHealthContextDto(
                age = profile?.age,
                gender = profile?.gender.orEmpty(),
                heightCm = profile?.heightCm,
                weightKg = profile?.weightKg,
                healthGoals = profile?.healthGoals ?: emptyList()
            )
        )
        val response = RetrofitInstance.aiService.sendMessage("Bearer $token", request)
        val aiMessage = ChatMessage(
            sender = MessageSender.AI,
            text = response.reply.ifBlank { "I am currently syncing. Please ask your health question once more." },
            isUrgentWarning = response.isUrgentWarning
        )
        persistMessage(uid, aiMessage)
        aiMessage
    }
}
