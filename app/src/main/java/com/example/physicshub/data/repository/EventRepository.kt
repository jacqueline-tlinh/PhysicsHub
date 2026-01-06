package com.example.physicshub.data.repository

import android.content.Context
import com.example.physicshub.data.local.AppDatabase
import com.example.physicshub.data.local.EventEntity
import com.example.physicshub.data.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime

class EventRepository private constructor(context: Context) {

    private val eventDao = AppDatabase.getDatabase(context).eventDao()

    // Flow of all events
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()
        .map { entities -> entities.map { it.toEvent() } }

    // Flow of upcoming events
    fun getUpcomingEvents(limit: Int = 10): Flow<List<Event>> {
        return eventDao.getUpcomingEvents(LocalDate.now(), limit)
            .map { entities -> entities.map { it.toEvent() } }
    }

    // Flow of events by date
    fun getEventsByDate(date: LocalDate): Flow<List<Event>> {
        return eventDao.getEventsByDate(date)
            .map { entities -> entities.map { it.toEvent() } }
    }

    // Get single event by ID
    suspend fun getEventById(eventId: String): Event? {
        return eventDao.getEventById(eventId)?.toEvent()
    }

    // Create new event
    suspend fun createEvent(
        name: String,
        date: LocalDate,
        time: LocalTime,
        location: String,
        note: String
    ): Result<Event> {
        return try {
            val event = Event(
                name = name,
                date = date,
                time = time,
                location = location,
                note = note
            )
            val entity = EventEntity.fromEvent(event)
            eventDao.insertEvent(entity)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update existing event
    suspend fun updateEvent(event: Event): Result<Unit> {
        return try {
            val entity = EventEntity.fromEvent(event)
            eventDao.updateEvent(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete event
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventDao.deleteEventById(eventId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Register user for event
    suspend fun registerForEvent(eventId: String, userName: String): Result<Event> {
        return try {
            val event = getEventById(eventId)
                ?: return Result.failure(Exception("Event not found"))

            // Check if user already registered
            if (event.registeredUsers.contains(userName)) {
                return Result.failure(Exception("User already registered"))
            }

            val updatedEvent = event.copy(
                registeredUsers = event.registeredUsers + userName
            )
            updateEvent(updatedEvent)
            Result.success(updatedEvent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all dates with events
    suspend fun getDatesWithEvents(): Set<LocalDate> {
        return eventDao.getAllEventDates().toSet()
    }

    // Clear all events (for testing)
    suspend fun clearAllEvents() {
        eventDao.deleteAllEvents()
    }

    // Initialize with sample data
    suspend fun initializeSampleData() {
        val count = eventDao.getEventCount()
        if (count == 0) {
            val sampleEvents = listOf(
                Event(
                    name = "AI & Machine Learning Seminar",
                    date = LocalDate.now().plusDays(2),
                    time = LocalTime.of(14, 0),
                    location = "Room A101",
                    note = "Guest speaker from Google AI Research. Topics include deep learning and neural networks."
                ),
                Event(
                    name = "Quantum Physics Workshop",
                    date = LocalDate.now().plusDays(5),
                    time = LocalTime.of(10, 30),
                    location = "Physics Lab Building",
                    note = "Hands-on experiments with quantum mechanics principles. Bring your lab notebook."
                ),
                Event(
                    name = "Career Fair 2025",
                    date = LocalDate.now(),
                    time = LocalTime.of(9, 0),
                    location = "Main Hall",
                    note = "Meet recruiters from top tech companies. Prepare your resume and portfolio."
                ),
                Event(
                    name = "Mathematics Competition",
                    date = LocalDate.now().plusDays(1),
                    time = LocalTime.of(13, 0),
                    location = "Room B205",
                    note = "Regional mathematics olympiad qualifying round. Registration required."
                ),
                Event(
                    name = "Full-Stack Coding Bootcamp",
                    date = LocalDate.now().plusDays(3),
                    time = LocalTime.of(15, 30),
                    location = "Computer Lab 3",
                    note = "Learn React, Node.js, and MongoDB. Beginners welcome. Laptops required."
                )
            )

            sampleEvents.forEach { event ->
                eventDao.insertEvent(EventEntity.fromEvent(event))
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: EventRepository? = null

        fun getInstance(context: Context): EventRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = EventRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}