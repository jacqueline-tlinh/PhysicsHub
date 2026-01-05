package com.example.physicshub.data.repository

import com.example.physicshub.data.model.ExamMetadata
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MetadataRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getExamMetadata(): List<ExamMetadata> {
        val snapshot = firestore
            .collection("exam_metadata")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            try {
                val obj = doc.toObject(ExamMetadata::class.java)
                println("METADATA DOC OK: ${doc.id}")
                obj
            } catch (e: Exception) {
                println("METADATA DOC FAILED: ${doc.id}")
                e.printStackTrace()
                null
            }
        }
    }

}