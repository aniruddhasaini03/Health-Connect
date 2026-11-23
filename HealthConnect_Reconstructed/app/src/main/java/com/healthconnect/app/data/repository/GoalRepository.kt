package com.healthconnect.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.HealthGoal
import kotlinx.coroutines.tasks.await

class GoalRepository(private val firestore: FirebaseFirestore = FirebaseModule.firestore) {
    private fun goalsCollection(uid: String) = firestore.collection("users").document(uid).collection("goals")

    suspend fun createGoal(uid: String, goal: HealthGoal): Result<HealthGoal> = runCatching {
        val doc = goalsCollection(uid).document()
        val saved = goal.copy(id = doc.id)
        doc.set(saved).await(); saved
    }

    suspend fun getGoals(uid: String): Result<List<HealthGoal>> = runCatching {
        goalsCollection(uid).get().await().documents.mapNotNull { it.toObject(HealthGoal::class.java) }
            .sortedByDescending { it.createdAt }
    }

    suspend fun updateGoal(uid: String, goal: HealthGoal): Result<Unit> = runCatching {
        goalsCollection(uid).document(goal.id).set(goal).await()
    }

    suspend fun deleteGoal(uid: String, goalId: String): Result<Unit> = runCatching {
        goalsCollection(uid).document(goalId).delete().await()
    }
}
