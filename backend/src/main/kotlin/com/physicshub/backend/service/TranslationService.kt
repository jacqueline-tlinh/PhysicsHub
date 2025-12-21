package com.physicshub.backend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.physicshub.backend.model.TranslationResponse
import com.physicshub.backend.model.TranslationStrings
import org.springframework.stereotype.Service
import java.io.File
import jakarta.annotation.PostConstruct

@Service
class TranslationService(
    private val objectMapper: ObjectMapper
) {
    private val filePath = "translations.json"
    private var translations: TranslationResponse = TranslationResponse()

    @PostConstruct
    fun init() {
        loadTranslations()
    }

    private fun loadTranslations() {
        val file = File(filePath)
        if (file.exists()) {
            translations = objectMapper.readValue(file, TranslationResponse::class.java)
        } else {
            translations = getDefaultTranslations()
            saveTranslations()
        }
    }

    private fun saveTranslations() {
        val file = File(filePath)
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, translations)
    }

    fun getAllTranslations(): TranslationResponse {
        return translations
    }

    fun getTranslationsByLanguage(language: String): TranslationStrings? {
        return when (language.lowercase()) {
            "en" -> translations.en
            "vn", "vi" -> translations.vn
            else -> null
        }
    }

    fun updateTranslation(language: String, key: String, value: String): Boolean {
        val strings = when (language.lowercase()) {
            "en" -> translations.en
            "vn", "vi" -> translations.vn
            else -> return false
        }

        val updated = updateField(strings, key, value)
        if (updated) {
            saveTranslations()
        }
        return updated
    }

    fun updateAllTranslations(newTranslations: TranslationResponse): TranslationResponse {
        translations = newTranslations
        saveTranslations()
        return translations
    }

    fun updateLanguageTranslations(language: String, strings: TranslationStrings): Boolean {
        when (language.lowercase()) {
            "en" -> translations.en = strings
            "vn", "vi" -> translations.vn = strings
            else -> return false
        }
        saveTranslations()
        return true
    }

    private fun updateField(strings: TranslationStrings, key: String, value: String): Boolean {
        return when (key) {
            "hello" -> { strings.hello = value; true }
            "noticeBoard" -> { strings.noticeBoard = value; true }
            "viewMore" -> { strings.viewMore = value; true }
            "upcomingEvent" -> { strings.upcomingEvent = value; true }
            "equipmentBooking" -> { strings.equipmentBooking = value; true }
            "notifications" -> { strings.notifications = value; true }
            "all" -> { strings.all = value; true }
            "unread" -> { strings.unread = value; true }
            "noUnreadNotices" -> { strings.noUnreadNotices = value; true }
            "noNotices" -> { strings.noNotices = value; true }
            "back" -> { strings.back = value; true }
            "placeholder" -> { strings.placeholder = value; true }
            "academicAffairs" -> { strings.academicAffairs = value; true }
            "research" -> { strings.research = value; true }
            "events" -> { strings.events = value; true }
            "general" -> { strings.general = value; true }
            else -> false
        }
    }

    private fun getDefaultTranslations(): TranslationResponse {
        return TranslationResponse(
            en = TranslationStrings(
                hello = "Hello,",
                noticeBoard = "NOTICE BOARD",
                viewMore = "View more",
                upcomingEvent = "UPCOMING EVENT",
                equipmentBooking = "EQUIPMENT BOOKING",
                notifications = "Notifications",
                all = "All",
                unread = "Unread",
                noUnreadNotices = "No unread notices",
                noNotices = "No notices",
                back = "Back",
                placeholder = "Placeholder",
                academicAffairs = "Academic Affairs",
                research = "Research",
                events = "Events",
                general = "General"
            ),
            vn = TranslationStrings(
                hello = "Xin chào,",
                noticeBoard = "THÔNG BÁO",
                viewMore = "Xem thêm",
                upcomingEvent = "SỰ KIỆN SẮP TỚI",
                equipmentBooking = "ĐẶT THIẾT BỊ",
                notifications = "Thông báo",
                all = "Tất cả",
                unread = "Chưa đọc",
                noUnreadNotices = "Không có thông báo chưa đọc",
                noNotices = "Không có thông báo",
                back = "Quay lại",
                placeholder = "Đang cập nhật",
                academicAffairs = "Học vụ",
                research = "Nghiên cứu",
                events = "Sự kiện",
                general = "Chung"
            )
        )
    }
}
