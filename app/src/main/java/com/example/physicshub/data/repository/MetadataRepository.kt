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
            doc.toObject(ExamMetadata::class.java)
        }
    }
}
