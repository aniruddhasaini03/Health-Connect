package com.healthconnect.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.healthconnect.app.data.firebase.FirebaseModule
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseModule.auth
) {
    fun isAuthenticated(): Boolean = auth.currentUser != null
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password).await().user
            ?: error("Login failed. Please try again.")
    }

    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser> = runCatching {
        val user = auth.createUserWithEmailAndPassword(email.trim(), password).await().user
            ?: error("Registration failed. Please try again.")
        userRepository.createInitialProfile(user.uid, name.trim(), email.trim()).getOrThrow()
        user
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    fun logout() = auth.signOut()
}
