package com.example.physicshub.ui.screens.notices

import androidx.compose.ui.graphics.Color

enum class NoticeCategory(
    val displayName: String,
    val backgroundColor: Color,
    val iconTint: Color
) {
    ACADEMIC_AFFAIRS(
        displayName = "Academic Affairs",
        backgroundColor = Color(0xFFE8F5E9),
        iconTint = Color(0xFF27BA80)
    ),
    RESEARCH(
        displayName = "Research",
        backgroundColor = Color(0xFFFFFDE7),
        iconTint = Color(0xFF27BA80)
    ),
    EVENTS(
        displayName = "Events",
        backgroundColor = Color(0xFFE3F2FD),
        iconTint = Color(0xFF27BA80)
    ),
    GENERAL(
        displayName = "General",
        backgroundColor = Color(0xFFF3E5F5),
        iconTint = Color(0xFF27BA80)
    )
}

data class Notice(
    val id: String,
    val category: NoticeCategory,
    val title: String,
    val content: String,
    val date: String,
    val isRead: Boolean = false
)

val mockNotices = listOf(
    Notice(
        id = "1",
        category = NoticeCategory.ACADEMIC_AFFAIRS,
        title = "End of Semester Examination Schedule",
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        date = "Dec 20, 2025",
        isRead = false
    ),
    Notice(
        id = "2",
        category = NoticeCategory.RESEARCH,
        title = "Research Grant Application Deadline",
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.",
        date = "Dec 18, 2025",
        isRead = true
    ),
    Notice(
        id = "3",
        category = NoticeCategory.EVENTS,
        title = "Physics Department Annual Meetup",
        content = "Join us for the annual physics department gathering with guest speakers and networking opportunities.",
        date = "Dec 15, 2025",
        isRead = false
    ),
    Notice(
        id = "4",
        category = NoticeCategory.ACADEMIC_AFFAIRS,
        title = "Course Registration Opens",
        content = "Spring semester course registration will open next week. Please review available courses.",
        date = "Dec 14, 2025",
        isRead = true
    ),
    Notice(
        id = "5",
        category = NoticeCategory.GENERAL,
        title = "Library Hours Update",
        content = "The library will have extended hours during the examination period.",
        date = "Dec 12, 2025",
        isRead = false
    ),
    Notice(
        id = "6",
        category = NoticeCategory.RESEARCH,
        title = "New Lab Equipment Available",
        content = "State-of-the-art spectrometers are now available for research projects.",
        date = "Dec 10, 2025",
        isRead = true
    )
)
