package com.example.physicshub.data.repository

import android.net.Uri
import com.example.physicshub.data.model.ExamPaper
import com.example.physicshub.data.model.FileType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ExamUploadRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadExamPaper(
        fileUri: Uri,
        fileType: FileType,
        fileSize: Long,

        division: String,
        category: String,
        course: String,
        examType: String,
        semester: String,
        year: Int,

        uploadedBy: String,
        role: String
    ): Result<Unit> {
        return try {
            val paperId = UUID.randomUUID().toString()
            val extension = when (fileType) {
                FileType.PDF -> "pdf"
                FileType.IMAGE -> "jpg"
            }

            val storagePath =
                "exam_papers/$division/$category/$course/$year/$semester/$examType/$paperId.$extension"

            val storageRef = storage.reference.child(storagePath)
            storageRef.putFile(fileUri).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()

            val paper = ExamPaper(
                id = paperId,
                course = course,
                examType = examType,
                semester = semester,
                year = year,
                fileUrl = downloadUrl,
                fileType = fileType,
                fileSize = fileSize,
                uploadedBy = uploadedBy,
                role = role,
                verified = role == "admin",
                uploadedAt = Timestamp.now()
            )

            firestore.collection("exam_papers")
                .document(paperId)
                .set(paper)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
