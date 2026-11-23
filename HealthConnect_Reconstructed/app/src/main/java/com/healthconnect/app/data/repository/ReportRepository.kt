package com.healthconnect.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.MedicalReport
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReportRepository(
    private val firestore: FirebaseFirestore = FirebaseModule.firestore,
    private val auth: FirebaseAuth = FirebaseModule.auth
) {
    private fun uid(explicit: String? = null) = explicit ?: auth.currentUser?.uid ?: "anonymous"
    private fun reportsCollection(explicitUid: String? = null) =
        firestore.collection("users").document(uid(explicitUid)).collection("reports")

    suspend fun addReport(report: MedicalReport, explicitUid: String? = null): Result<MedicalReport> = runCatching {
        val doc = reportsCollection(explicitUid).document()
        val saved = report.copy(id = doc.id, userId = uid(explicitUid))
        doc.set(saved).await(); saved
    }

    suspend fun saveReport(report: MedicalReport, explicitUid: String? = null): Result<Unit> = runCatching {
        val id = report.id.ifBlank { reportsCollection(explicitUid).document().id }
        reportsCollection(explicitUid).document(id).set(report.copy(id = id, userId = uid(explicitUid))).await()
    }

    suspend fun deleteReport(reportId: String, explicitUid: String? = null): Result<Unit> = runCatching {
        reportsCollection(explicitUid).document(reportId).delete().await()
    }

    fun getReports(explicitUid: String? = null): Flow<Result<List<MedicalReport>>> = callbackFlow {
        val listener = reportsCollection(explicitUid).orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) trySend(Result.failure(error))
                else trySend(Result.success(value?.documents?.mapNotNull { it.toObject(MedicalReport::class.java) } ?: emptyList()))
            }
        awaitClose { listener.remove() }
    }
}
