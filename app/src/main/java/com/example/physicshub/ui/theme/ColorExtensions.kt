package com.example.physicshub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Helper object để truy cập các màu custom dễ dàng hơn
 * Tự động chuyển đổi theo theme Light/Dark
 */
object AppColors {

    // Notice Colors
    val noticeCategoryAcademic: Color
        @Composable
        get() = MaterialTheme.extendedColors.noticeCategoryAcademic

    val noticeCategoryResearch: Color
        @Composable
        get() = MaterialTheme.extendedColors.noticeCategoryResearch

    val noticeCategoryEvents: Color
        @Composable
        get() = MaterialTheme.extendedColors.noticeCategoryEvents

    val noticeCategoryGeneral: Color
        @Composable
        get() = MaterialTheme.extendedColors.noticeCategoryGeneral

    val noticeIconTint: Color
        @Composable
        get() = MaterialTheme.extendedColors.noticeIconTint

    // Exam Colors
    val examArchiveBlue: Color
        @Composable
        get() = MaterialTheme.extendedColors.examArchiveBlue

    val examUploadGreen: Color
        @Composable
        get() = MaterialTheme.extendedColors.examUploadGreen

    val examCategoryBlue: Color
        @Composable
        get() = MaterialTheme.extendedColors.examCategoryBlue

    val examEmptyState: Color
        @Composable
        get() = MaterialTheme.extendedColors.examEmptyState

    // Event Colors
    val eventCardBackground: Color
        @Composable
        get() = MaterialTheme.extendedColors.eventCardBackground
}

/**
 * Extension functions cho các use cases cụ thể
 */

@Composable
fun getCategoryColor(alpha: Float = 1f): Color {
    return MaterialTheme.extendedColors.examCategoryBlue.copy(alpha = alpha)
}

@Composable
fun getEmptyStateColor(): Color {
    return MaterialTheme.extendedColors.examEmptyState
}