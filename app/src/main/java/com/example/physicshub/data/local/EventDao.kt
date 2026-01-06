package com.example.physicshub.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE date >= :currentDate ORDER BY date ASC, time ASC LIMIT :limit")
    fun getUpcomingEvents(currentDate: LocalDate, limit: Int = 10): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE date = :date ORDER BY time ASC")
    fun getEventsByDate(date: LocalDate): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: String)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int

    @Query("SELECT DISTINCT date FROM events ORDER BY date ASC")
    suspend fun getAllEventDates(): List<LocalDate>
}