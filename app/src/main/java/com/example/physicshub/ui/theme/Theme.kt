package com.example.physicshub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Extended Colors cho app - giữ nguyên màu từ UI hiện tại
 */
@Immutable
data class ExtendedColors(
    // Notice Category Colors
    val noticeCategoryAcademic: Color,
    val noticeCategoryResearch: Color,
    val noticeCategoryEvents: Color,
    val noticeCategoryGeneral: Color,
    val noticeIconTint: Color,

    // Exam Section Colors
    val examArchiveBlue: Color,
    val examUploadGreen: Color,
    val examCategoryBlue: Color,
    val examEmptyState: Color,

    // Event Colors
    val eventCardBackground: Color
)

val LightExtendedColors = ExtendedColors(
    noticeCategoryAcademic = NoticeCategoryAcademic,
    noticeCategoryResearch = NoticeCategoryResearch,
    noticeCategoryEvents = NoticeCategoryEvents,
    noticeCategoryGeneral = NoticeCategoryGeneral,
    noticeIconTint = NoticeIconTint,
    examArchiveBlue = ExamArchiveBlue,
    examUploadGreen = ExamUploadGreen,
    examCategoryBlue = ExamCategoryBlue,
    examEmptyState = ExamEmptyState,
    eventCardBackground = EventCardBackground
)

val DarkExtendedColors = ExtendedColors(
    noticeCategoryAcademic = DarkNoticeCategoryAcademic,
    noticeCategoryResearch = DarkNoticeCategoryResearch,
    noticeCategoryEvents = DarkNoticeCategoryEvents,
    noticeCategoryGeneral = DarkNoticeCategoryGeneral,
    noticeIconTint = DarkNoticeIconTint,
    examArchiveBlue = DarkExamArchiveBlue,
    examUploadGreen = DarkExamUploadGreen,
    examCategoryBlue = DarkExamCategoryBlue,
    examEmptyState = DarkExamEmptyState,
    eventCardBackground = Color(0xFF1B3A2F)
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * Light Mode Color Scheme
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5F0),
    onPrimaryContainer = Color(0xFF004D33),

    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1F0E5),
    onSecondaryContainer = Color(0xFF003826),

    tertiary = TertiaryGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE3F9F1),
    onTertiaryContainer = Color(0xFF00522E),

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF2F2F2),
    onSurfaceVariant = Color(0xFF616161),

    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFDECEB),
    onErrorContainer = Color(0xFF8C0009),

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0)
)

/**
 * Dark Mode Color Scheme
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,
    onPrimary = Color(0xFF003826),
    primaryContainer = Color(0xFF00522E),
    onPrimaryContainer = Color(0xFFD1F0E5),

    secondary = DarkSecondaryGreen,
    onSecondary = Color(0xFF003826),
    secondaryContainer = Color(0xFF004D33),
    onSecondaryContainer = Color(0xFFE8F5F0),

    tertiary = DarkTertiaryGreen,
    onTertiary = Color(0xFF00331D),
    tertiaryContainer = Color(0xFF004D33),
    onTertiaryContainer = Color(0xFFE3F9F1),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE5E5E5),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE5E5E5),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB8B8B8),

    error = Color(0xFFCF6679),
    onError = Color(0xFF1A1A1A),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF616161)
)

/**
 * PhysicsHub Theme với Extended Colors
 */
@Composable
fun PhysicsHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    androidx.compose.runtime.CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Extension property để truy cập extended colors dễ dàng
 * Sử dụng: MaterialTheme.extendedColors.examArchiveBlue
 */
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current