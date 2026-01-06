package com.example.physicshub.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyViewedDao {

    @Query("SELECT * FROM recently_viewed ORDER BY viewedAt DESC LIMIT :limit")
    fun getRecentlyViewed(limit: Int = 10): Flow<List<RecentlyViewedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyViewed(entity: RecentlyViewedEntity)

    @Query("DELETE FROM recently_viewed WHERE examId = :examId")
    suspend fun deleteRecentlyViewed(examId: String)

    @Query("DELETE FROM recently_viewed")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM recently_viewed")
    suspend fun getCount(): Int
}