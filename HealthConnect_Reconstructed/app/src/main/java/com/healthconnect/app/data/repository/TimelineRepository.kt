package com.healthconnect.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.TimelineEvent
import kotlinx.coroutines.tasks.await

class TimelineRepository(private val firestore: FirebaseFirestore = FirebaseModule.firestore) {
    private fun timelineCollection(uid: String) = firestore.collection("users").document(uid).collection("timelineEvents")

    suspend fun addEvent(uid: String, event: TimelineEvent): Result<TimelineEvent> = runCatching {
        val doc = timelineCollection(uid).document()
        val saved = event.copy(id = doc.id)
        doc.set(saved).await(); saved
    }

    suspend fun getEvents(uid: String): Result<List<TimelineEvent>> = runCatching {
        timelineCollection(uid).get().await().documents.mapNotNull { it.toObject(TimelineEvent::class.java) }
            .sortedByDescending { it.timestamp }
    }
}
