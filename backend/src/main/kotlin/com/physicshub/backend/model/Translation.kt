package com.physicshub.backend.model

data class TranslationStrings(
    var hello: String = "",
    var noticeBoard: String = "",
    var viewMore: String = "",
    var upcomingEvent: String = "",
    var equipmentBooking: String = "",
    var notifications: String = "",
    var all: String = "",
    var unread: String = "",
    var noUnreadNotices: String = "",
    var noNotices: String = "",
    var back: String = "",
    var placeholder: String = "",
    var academicAffairs: String = "",
    var research: String = "",
    var events: String = "",
    var general: String = ""
)

data class TranslationResponse(
    var en: TranslationStrings = TranslationStrings(),
    var vn: TranslationStrings = TranslationStrings()
)

data class UpdateTranslationRequest(
    val language: String,
    val key: String,
    val value: String
)
