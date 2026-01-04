package com.example.physicshub.data.model

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    val location: String,
    val note: String,
    val registeredUsers: List<String> = emptyList()
)

data class EventRegistration(
    val eventId: String,
    val userName: String,
    val registrationDate: LocalDate = LocalDate.now()
)