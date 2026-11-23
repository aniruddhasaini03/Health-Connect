package com.healthconnect.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseModule.firestore
) {
    private val usersCollection = firestore.collection("users")

    suspend fun createInitialProfile(uid: String, name: String, email: String): Result<User> = runCatching {
        val user = User(uid = uid, name = name, email = email)
        usersCollection.document(uid).set(user).await()
        user
    }

    suspend fun getProfile(uid: String): Result<User> = runCatching {
        usersCollection.document(uid).get().await().toObject(User::class.java)
            ?: error("Profile not found.")
    }

    suspend fun updateProfile(user: User): Result<Unit> = runCatching {
        usersCollection.document(user.uid).set(user.copy(updatedAt = System.currentTimeMillis())).await()
    }
}
