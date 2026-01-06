package com.example.physicshub.ui.screens.notices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID

class NoticeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NoticeRepository.getInstance(application)

    // State flows
    private val _notices = MutableStateFlow<List<Notice>>(emptyList())
    val notices: StateFlow<List<Notice>> = _notices.asStateFlow()

    private val _expandedNoticeIds = MutableStateFlow<Set<String>>(emptySet())
    val expandedNoticeIds: StateFlow<Set<String>> = _expandedNoticeIds.asStateFlow()

    private val _showUnreadOnly = MutableStateFlow(false)
    val showUnreadOnly: StateFlow<Boolean> = _showUnreadOnly.asStateFlow()

    private val _isCreatingNotice = MutableStateFlow(false)
    val isCreatingNotice: StateFlow<Boolean> = _isCreatingNotice.asStateFlow()

    init {
        loadNotices()
    }

    private fun loadNotices() {
        viewModelScope.launch {
            combine(
                repository.cachedNotices,
                repository.readNoticeIds
            ) { cachedNotices, readIds ->
                cachedNotices.map { notice ->
                    notice.copy(isRead = readIds.contains(notice.id))
                }
            }.collect { noticesWithReadStatus ->
                _notices.value = noticesWithReadStatus
            }
        }
    }

    fun getFilteredNotices(): List<Notice> {
        return if (_showUnreadOnly.value) {
            _notices.value.filter { !it.isRead }
        } else {
            _notices.value
        }
    }

    fun toggleUnreadFilter() {
        _showUnreadOnly.value = !_showUnreadOnly.value
    }

    fun setUnreadFilter(showUnreadOnly: Boolean) {
        _showUnreadOnly.value = showUnreadOnly
    }

    fun toggleNoticeExpansion(noticeId: String) {
        val currentExpanded = _expandedNoticeIds.value
        _expandedNoticeIds.value = if (currentExpanded.contains(noticeId)) {
            currentExpanded - noticeId
        } else {
            currentExpanded + noticeId
        }
    }

    fun isNoticeExpanded(noticeId: String): Boolean {
        return _expandedNoticeIds.value.contains(noticeId)
    }

    fun toggleReadState(noticeId: String) {
        viewModelScope.launch {
            val notice = _notices.value.find { it.id == noticeId }
            if (notice != null) {
                if (notice.isRead) {
                    repository.markAsUnread(noticeId)
                } else {
                    repository.markAsRead(noticeId)
                }
            }
        }
    }

    fun markAsRead(noticeId: String) {
        viewModelScope.launch {
            repository.markAsRead(noticeId)
        }
    }

    fun markAsUnread(noticeId: String) {
        viewModelScope.launch {
            repository.markAsUnread(noticeId)
        }
    }

    fun createNotice(
        category: NoticeCategory,
        title: String,
        content: String,
        date: String
    ) {
        viewModelScope.launch {
            val newNotice = Notice(
                id = UUID.randomUUID().toString(),
                category = category,
                title = title,
                content = content,
                date = date,
                isRead = false
            )

            val updatedNotices = listOf(newNotice) + _notices.value
            repository.saveNotices(updatedNotices)
        }
    }

    fun updateNotice(
        noticeId: String,
        category: NoticeCategory,
        title: String,
        content: String,
        date: String
    ) {
        viewModelScope.launch {
            val updatedNotices = _notices.value.map { notice ->
                if (notice.id == noticeId) {
                    notice.copy(
                        category = category,
                        title = title,
                        content = content,
                        date = date
                    )
                } else {
                    notice
                }
            }
            repository.saveNotices(updatedNotices)
        }
    }

    fun deleteNotice(noticeId: String) {
        viewModelScope.launch {
            val updatedNotices = _notices.value.filter { it.id != noticeId }
            repository.saveNotices(updatedNotices)
            // Also remove from expanded set if it was expanded
            _expandedNoticeIds.value = _expandedNoticeIds.value - noticeId
        }
    }

    fun showCreateNoticeDialog() {
        _isCreatingNotice.value = true
    }

    fun hideCreateNoticeDialog() {
        _isCreatingNotice.value = false
    }

    fun clearReadStates() {
        viewModelScope.launch {
            repository.clearReadStates()
        }
    }

    fun getTotalCount(): Int = _notices.value.size

    fun getUnreadCount(): Int = _notices.value.count { !it.isRead }
}