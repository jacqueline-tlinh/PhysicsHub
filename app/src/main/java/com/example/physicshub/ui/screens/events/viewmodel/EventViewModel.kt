package com.example.physicshub.ui.screens.events.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physicshub.data.model.Event
import com.example.physicshub.data.model.EventRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime

class EventViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    // null = chưa chọn ngày nào (hiển thị tất cả events)
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    // Expose computed state để UI subscribe trực tiếp
    val eventsToDisplay: StateFlow<List<Event>> = _selectedDate
        .combine(_events) { date, eventsList ->
            if (date == null) {
                eventsList.sortedBy { it.date }
            } else {
                eventsList.filter { it.date == date }.sortedBy { it.time }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Upcoming events - filtered in ViewModel for Home Screen
    val upcomingEvents: StateFlow<List<Event>> = _events
        .map { eventsList ->
            val today = LocalDate.now()
            eventsList
                .filter { it.date >= today }
                .sortedWith(compareBy({ it.date }, { it.time }))
                .take(10) // Limit to 10 most recent upcoming events
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _registrations = MutableStateFlow<List<EventRegistration>>(emptyList())
    val registrations: StateFlow<List<EventRegistration>> = _registrations.asStateFlow()

    private val _showSuccessMessage = MutableStateFlow(false)
    val showSuccessMessage: StateFlow<Boolean> = _showSuccessMessage.asStateFlow()

    // Expand/Collapse states
    private val _isCalendarExpanded = MutableStateFlow(true)
    val isCalendarExpanded: StateFlow<Boolean> = _isCalendarExpanded.asStateFlow()

    private val _isEventListExpanded = MutableStateFlow(true)
    val isEventListExpanded: StateFlow<Boolean> = _isEventListExpanded.asStateFlow()

    init {
        // Add sample data for testing
        addSampleEvents()
    }

    private fun addSampleEvents() {
        // Sample events with English content
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
        _events.value = sampleEvents
    }

    fun selectDate(date: LocalDate?) {
        _selectedDate.value = date
    }

    fun getEventsForDate(date: LocalDate?): List<Event> {
        // Deprecated: Sử dụng eventsToDisplay StateFlow thay thế
        return if (date == null) {
            _events.value.sortedBy { it.date }
        } else {
            _events.value.filter { it.date == date }.sortedBy { it.time }
        }
    }

    fun getDatesWithEvents(): Set<LocalDate> {
        return _events.value.map { it.date }.toSet()
    }

    fun createEvent(
        name: String,
        date: LocalDate,
        time: LocalTime,
        location: String,
        note: String
    ) {
        val newEvent = Event(
            name = name,
            date = date,
            time = time,
            location = location,
            note = note
        )
        _events.value = _events.value + newEvent
    }

    fun deleteEvent(eventId: String) {
        _events.value = _events.value.filter { it.id != eventId }

        // Xóa các registrations liên quan đến event này
        _registrations.value = _registrations.value.filter { it.eventId != eventId }
    }

    fun registerForEvent(eventId: String, userName: String) {
        val registration = EventRegistration(
            eventId = eventId,
            userName = userName
        )
        _registrations.value = _registrations.value + registration

        // Update event with registered user
        _events.value = _events.value.map { event ->
            if (event.id == eventId) {
                event.copy(registeredUsers = event.registeredUsers + userName)
            } else {
                event
            }
        }

        _showSuccessMessage.value = true
    }

    fun getEventById(eventId: String): Event? {
        return _events.value.find { it.id == eventId }
    }

    fun resetSuccessMessage() {
        _showSuccessMessage.value = false
    }

    fun toggleCalendarExpanded() {
        _isCalendarExpanded.value = !_isCalendarExpanded.value
    }

    fun toggleEventListExpanded() {
        _isEventListExpanded.value = !_isEventListExpanded.value
    }
}