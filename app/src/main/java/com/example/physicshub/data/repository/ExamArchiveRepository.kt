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
     * Now uses courseID for more reliable querying
     */
    suspend fun getExamsByCourse(
        division: String,
        category: String,
        courseID: String  // Changed from course to courseID
    ): List<ExamPaper> {
        return try {
            println("🔍 Querying exams: division=$division, category=$category, courseID=$courseID")

            val snapshot = firestore
                .collection("exam_papers")
                .whereEqualTo("division", division)
                .whereEqualTo("category", category)
                .whereEqualTo("courseID", courseID)  // Query by courseID instead
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("examType", Query.Direction.ASCENDING)
                .get()
                .await()

            val papers = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ExamPaper::class.java)
            }

            println("📄 Found ${papers.size} exam papers")
            papers
        } catch (e: Exception) {
            println("❌ Error fetching exams: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Alternative: Get exams by course name (if you prefer this approach)
     */
    suspend fun getExamsByCourseName(
        division: String,
        category: String,
        courseName: String
    ): List<ExamPaper> {
        return try {
            println("🔍 Querying exams by name: division=$division, category=$category, course=$courseName")

            val snapshot = firestore
                .collection("exam_papers")
                .whereEqualTo("division", division)
                .whereEqualTo("category", category)
                .whereEqualTo("course", courseName)
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("examType", Query.Direction.ASCENDING)
                .get()
                .await()

            val papers = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ExamPaper::class.java)
            }

            println("📄 Found ${papers.size} exam papers")
            papers
        } catch (e: Exception) {
            println("❌ Error fetching exams: ${e.message}")
            e.printStackTrace()
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