package com.example.physicshub.data.model

data class ExamMetadata(
    val title: String = "",
    val divisions: List<DivisionMetadata> = emptyList()
)

data class DivisionMetadata(
    val name: String,
    val categories: List<CategoryMetadata>
)

data class CategoryMetadata(
    val name: String,
    val courses: List<String>
)
