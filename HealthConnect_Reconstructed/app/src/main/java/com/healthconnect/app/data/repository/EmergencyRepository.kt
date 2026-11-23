package com.healthconnect.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.healthconnect.app.data.firebase.FirebaseModule
import com.healthconnect.app.data.model.EmergencyContact
import kotlinx.coroutines.tasks.await

class EmergencyRepository(private val firestore: FirebaseFirestore = FirebaseModule.firestore) {
    private fun contactsCollection(uid: String) = firestore.collection("users").document(uid).collection("emergencyContacts")

    suspend fun addContact(uid: String, contact: EmergencyContact): Result<EmergencyContact> = runCatching {
        val doc = contactsCollection(uid).document()
        val saved = contact.copy(id = doc.id)
        doc.set(saved).await(); saved
    }

    suspend fun getContacts(uid: String): Result<List<EmergencyContact>> = runCatching {
        contactsCollection(uid).get().await().documents.mapNotNull { it.toObject(EmergencyContact::class.java) }
    }

    suspend fun deleteContact(uid: String, contactId: String): Result<Unit> = runCatching {
        contactsCollection(uid).document(contactId).delete().await()
    }
}
