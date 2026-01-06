package com.example.physicshub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey
    val examId: String,
    val course: String,
    val examType: String,
    val year: Int,
    val viewedAt: Long = System.currentTimeMillis()
)