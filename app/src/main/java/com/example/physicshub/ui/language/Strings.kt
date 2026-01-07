package com.example.physicshub.ui.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

val LocalTranslations = compositionLocalOf {
    Pair(defaultEnglishStrings, defaultVietnameseStrings)
}

object Strings {
    private val currentStrings: TranslationStrings
        @Composable @ReadOnlyComposable
        get() {
            val (en, vn) = LocalTranslations.current
            return when (LocalLanguage.current) {
                Language.ENGLISH -> en
                Language.VIETNAMESE -> vn
            }
        }

    val hello: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.hello.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.hello
            else defaultVietnameseStrings.hello
        }

    val noticeBoard: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.noticeBoard.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.noticeBoard
            else defaultVietnameseStrings.noticeBoard
        }

    val viewMore: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.viewMore.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.viewMore
            else defaultVietnameseStrings.viewMore
        }

    val upcomingEvent: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.upcomingEvent.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.upcomingEvent
            else defaultVietnameseStrings.upcomingEvent
        }

    val examArchive: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.examArchive.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.examArchive
            else defaultVietnameseStrings.examArchive
        }

    val notifications: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.notifications.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.notifications
            else defaultVietnameseStrings.notifications
        }

    val all: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.all.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.all
            else defaultVietnameseStrings.all
        }

    val unread: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.unread.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.unread
            else defaultVietnameseStrings.unread
        }

    val noUnreadNotices: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.noUnreadNotices.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.noUnreadNotices
            else defaultVietnameseStrings.noUnreadNotices
        }

    val noNotices: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.noNotices.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.noNotices
            else defaultVietnameseStrings.noNotices
        }

    val back: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.back.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.back
            else defaultVietnameseStrings.back
        }

    val placeholder: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.placeholder.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.placeholder
            else defaultVietnameseStrings.placeholder
        }

    val academicAffairs: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.academicAffairs.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.academicAffairs
            else defaultVietnameseStrings.academicAffairs
        }

    val research: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.research.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.research
            else defaultVietnameseStrings.research
        }

    val events: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.events.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.events
            else defaultVietnameseStrings.events
        }

    val general: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.general.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.general
            else defaultVietnameseStrings.general
        }

    val navHome: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.navHome.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.navHome
            else defaultVietnameseStrings.navHome
        }

    val navEvents: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.navEvents.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.navEvents
            else defaultVietnameseStrings.navEvents
        }

    val navNotices: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.navNotices.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.navNotices
            else defaultVietnameseStrings.navNotices
        }

    val navExams: String
        @Composable @ReadOnlyComposable
        get() = currentStrings.navExams.ifEmpty {
            if (LocalLanguage.current == Language.ENGLISH) defaultEnglishStrings.navExams
            else defaultVietnameseStrings.navExams
        }

}
