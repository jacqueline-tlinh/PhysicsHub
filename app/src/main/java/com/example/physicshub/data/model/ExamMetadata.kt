package com.example.physicshub.data.model

data class ExamMetadata(
    val title: String = "",
    val categories: List<CategoryMetadata> = emptyList()
)

data class CategoryMetadata(
    val name: String = "",
    val courses: List<CourseMetadata> = emptyList()
)

data class CourseMetadata(
    val name: String = "",
    val courseID: String = ""
)