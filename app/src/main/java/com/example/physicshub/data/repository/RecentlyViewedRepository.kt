package com.example.physicshub.data.repository

import com.example.physicshub.data.local.RecentlyViewedDao
import com.example.physicshub.data.local.RecentlyViewedEntity
import kotlinx.coroutines.flow.Flow

class RecentlyViewedRepository(
    private val dao: RecentlyViewedDao
) {

    fun getRecentlyViewed(limit: Int = 10): Flow<List<RecentlyViewedEntity>> {
        return dao.getRecentlyViewed(limit)
    }

    suspend fun addRecentlyViewed(
        examId: String,
        course: String,
        examType: String,
        year: Int
    ) {
        dao.insertRecentlyViewed(
            RecentlyViewedEntity(
                examId = examId,
                course = course,
                examType = examType,
                year = year,
                viewedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}