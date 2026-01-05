package com.example.physicshub.data.repository

import android.net.Uri
import com.example.physicshub.data.model.ExamPaper
import com.example.physicshub.data.model.FileType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ExamUploadRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    /**
     * Upload a single exam paper file
     */
    suspend fun uploadExamPaper(
        fileUri: Uri,
        fileType: FileType,
        fileSize: Long,

        division: String,
        category: String,
        courseName: String,
        courseID: String,
        examType: String,
        year: Int,

        uploadedBy: String,
        role: String
    ): Result<String> {
        return try {
            val paperId = UUID.randomUUID().toString()
            val extension = when (fileType) {
                FileType.PDF -> "pdf"
                FileType.IMAGE -> "jpg"
            }

            // Storage path: exams/{division}/{category}/{courseID}/{year}/{examType}/{paperId}.{ext}
            // Using courseID for storage path for consistency
            val storagePath =
                "exams/$division/$category/$courseID/$year/$examType/$paperId.$extension"

            println("📤 Uploading to: $storagePath")

            val storageRef = storage.reference.child(storagePath)
            storageRef.putFile(fileUri).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()

            val paper = ExamPaper(
                id = paperId,
                division = division,
                category = category,
                course = courseName,      // Store the full name
                courseID = courseID,      // Store the ID for querying
                examType = examType,
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

            println("💾 Firestore document saved: $paperId")

            Result.success(paperId)
        } catch (e: Exception) {
            println("❌ Upload failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Upload multiple exam paper files (e.g., multiple images for one exam)
     * Returns list of successfully uploaded paper IDs
     */
    suspend fun uploadMultipleExamPapers(
        fileUris: List<Uri>,
        fileType: FileType,
        fileSizes: List<Long>,

        division: String,
        category: String,
        courseName: String,
        courseID: String,
        examType: String,
        year: Int,

        uploadedBy: String,
        role: String
    ): Result<List<String>> = coroutineScope {
        return@coroutineScope try {
            val uploadJobs = fileUris.mapIndexed { index, uri ->
                async {
                    uploadExamPaper(
                        fileUri = uri,
                        fileType = fileType,
                        fileSize = fileSizes.getOrElse(index) { 0L },
                        division = division,
                        category = category,
                        courseName = courseName,
                        courseID = courseID,
                        examType = examType,
                        year = year,
                        uploadedBy = uploadedBy,
                        role = role
                    )
                }
            }

            val results = uploadJobs.awaitAll()

            // Check if any failed
            val failures = results.filter { it.isFailure }
            if (failures.isNotEmpty()) {
                return@coroutineScope Result.failure(
                    Exception("${failures.size} file(s) failed to upload")
                )
            }

            // Extract successful IDs
            val successIds = results.mapNotNull { it.getOrNull() }
            Result.success(successIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}