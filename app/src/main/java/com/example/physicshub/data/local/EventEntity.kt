package com.example.physicshub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.physicshub.data.model.Event
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "events")
@TypeConverters(EventConverters::class)
data class EventEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    val location: String,
    val note: String,
    val registeredUsers: List<String>,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toEvent(): Event {
        return Event(
            id = id,
            name = name,
            date = date,
            time = time,
            location = location,
            note = note,
            registeredUsers = registeredUsers
        )
    }

    companion object {
        fun fromEvent(event: Event): EventEntity {
            return EventEntity(
                id = event.id,
                name = event.name,
                date = event.date,
                time = event.time,
                location = event.location,
                note = event.note,
                registeredUsers = event.registeredUsers
            )
        }
    }
}

class EventConverters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, dateFormatter)
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }

    @TypeConverter
    fun toLocalTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, timeFormatter)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
}