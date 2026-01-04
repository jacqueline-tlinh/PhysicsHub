package com.example.physicshub.data.model

import com.google.firebase.Timestamp

data class ExamPaper(
    val id: String = "",

    val division: String = "",
    val category: String = "",
    val course: String = "",
    val examType: String = "",
    val semester: String = "",
    val year: Int = 0,

    val fileUrl: String = "",
    val fileType: FileType = FileType.PDF,      // "pdf" | "image"
    val fileSize: Long = 0L,

    val uploadedBy: String = "",
    val role: String = "",          // "admin" | "student"
    val verified: Boolean = false,

    val uploadedAt: Timestamp = Timestamp.now()
)
