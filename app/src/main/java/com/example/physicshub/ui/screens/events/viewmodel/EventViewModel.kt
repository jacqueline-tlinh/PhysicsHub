package com.example.physicshub.ui.screens.events.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.physicshub.data.model.Event
import com.example.physicshub.data.repository.EventRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EventRepository.getInstance(application)

    // All events từ repository
    val events: StateFlow<List<Event>> = repository.allEvents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Upcoming events cho Home Screen
    val upcomingEvents: StateFlow<List<Event>> = repository.getUpcomingEvents(10)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Selected date (null = show all events)
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    // Events to display based on selected date
    val eventsToDisplay: StateFlow<List<Event>> = _selectedDate
        .combine(events) { date, eventsList ->
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

    // Dates with events
    val datesWithEvents: StateFlow<Set<LocalDate>> = events
        .map { eventsList -> eventsList.map { it.date }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // UI state
    private val _showSuccessMessage = MutableStateFlow(false)
    val showSuccessMessage: StateFlow<Boolean> = _showSuccessMessage.asStateFlow()

    private val _isCalendarExpanded = MutableStateFlow(true)
    val isCalendarExpanded: StateFlow<Boolean> = _isCalendarExpanded.asStateFlow()

    private val _isEventListExpanded = MutableStateFlow(true)
    val isEventListExpanded: StateFlow<Boolean> = _isEventListExpanded.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Initialize sample data nếu cần
        viewModelScope.launch {
            repository.initializeSampleData()
        }
    }

    // Select date
    fun selectDate(date: LocalDate?) {
        _selectedDate.value = date
    }

    // Create event
    fun createEvent(
        name: String,
        date: LocalDate,
        time: LocalTime,
        location: String,
        note: String
    ) {
        viewModelScope.launch {
            val result = repository.createEvent(name, date, time, location, note)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Delete event
    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            val result = repository.deleteEvent(eventId)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Register for event
    fun registerForEvent(eventId: String, userName: String) {
        viewModelScope.launch {
            val result = repository.registerForEvent(eventId, userName)
            if (result.isSuccess) {
                _showSuccessMessage.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Get event by ID
    fun getEventById(eventId: String): Event? {
        return events.value.find { it.id == eventId }
    }

    // Reset success message
    fun resetSuccessMessage() {
        _showSuccessMessage.value = false
    }

    // Clear error message
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Toggle calendar expanded
    fun toggleCalendarExpanded() {
        _isCalendarExpanded.value = !_isCalendarExpanded.value
    }

    // Toggle event list expanded
    fun toggleEventListExpanded() {
        _isEventListExpanded.value = !_isEventListExpanded.value
    }

    // Clear all events (for testing)
    fun clearAllEvents() {
        viewModelScope.launch {
            repository.clearAllEvents()
        }
    }
}