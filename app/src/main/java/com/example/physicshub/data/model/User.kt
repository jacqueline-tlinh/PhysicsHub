package com.example.physicshub.data.model

data class User(
    val studentId: String = "",
    val fullName: String = "",
    val password: String = "",  // initials + last 4 digits
    val role: String = "student"  // "student" or "admin"
)