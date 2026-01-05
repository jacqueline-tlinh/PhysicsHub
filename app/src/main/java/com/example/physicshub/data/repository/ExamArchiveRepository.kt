package com.example.physicshub.data.repository

import com.example.physicshub.data.model.ExamPaper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ExamArchiveRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Get all exam papers for a specific course
     */
    suspend fun getExamsByCourse(
        division: String,
        category: String,
        course: String
    ): List<ExamPaper> {
        return try {
            val snapshot = firestore
                .collection("exam_papers")
                .whereEqualTo("division", division)
                .whereEqualTo("category", category)
                .whereEqualTo("course", course)
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("examType", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ExamPaper::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get a single exam paper by ID
     */
    suspend fun getExamById(examId: String): ExamPaper? {
        return try {
            val snapshot = firestore
                .collection("exam_papers")
                .document(examId)
                .get()
                .await()

            snapshot.toObject(ExamPaper::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get newest uploads (for home screen)
     */
    suspend fun getNewestUploads(limit: Int = 10): List<ExamPaper> {
        return try {
            val snapshot = firestore
                .collection("exam_papers")
                .orderBy("uploadedAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ExamPaper::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}