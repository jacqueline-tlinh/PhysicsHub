package com.example.physicshub.data.model

data class ExamMetadata(
    val title: String = "",
    val divisions: List<String> = emptyList()
)

data class CategoryMetadata(
    val name: String,
    val courses: List<String>
)