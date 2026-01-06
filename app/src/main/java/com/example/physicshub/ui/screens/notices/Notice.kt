package com.example.physicshub.ui.screens.notices

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.physicshub.ui.theme.extendedColors

enum class NoticeCategory(
    val displayName: String
) {
    ACADEMIC_AFFAIRS("Academic Affairs"),
    RESEARCH("Research"),
    EVENTS("Events"),
    GENERAL("General");

    /**
     * Lấy màu background theo theme hiện tại
     */
    val backgroundColor: Color
        @Composable
        get() = when (this) {
            ACADEMIC_AFFAIRS -> MaterialTheme.extendedColors.noticeCategoryAcademic
            RESEARCH -> MaterialTheme.extendedColors.noticeCategoryResearch
            EVENTS -> MaterialTheme.extendedColors.noticeCategoryEvents
            GENERAL -> MaterialTheme.extendedColors.noticeCategoryGeneral
        }

    /**
     * Lấy màu icon theo theme hiện tại
     */
    val iconTint: Color
        @Composable
        get() = MaterialTheme.extendedColors.noticeIconTint
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