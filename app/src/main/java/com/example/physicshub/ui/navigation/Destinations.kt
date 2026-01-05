package com.example.physicshub.ui.navigation

sealed class Destinations(val route: String) {
    object Login : Destinations("login")
    object Home : Destinations("home")
    object Events : Destinations("events")
    object Notices : Destinations("notices")
    object Exams : Destinations("exams")

    // Exam Archive Navigation
    object ExamArchive : Destinations("exam_archive")

    object ExamCategory : Destinations("exam_category/{division}") {
        fun route(division: String) = "exam_category/$division"
    }

    object ExamCourse : Destinations("exam_course/{division}/{category}") {
        fun route(division: String, category: String) = "exam_course/$division/$category"
    }

    object ExamFiles : Destinations("exam_files/{division}/{category}/{courseID}") {
        fun route(division: String, category: String, courseID: String) =
            "exam_files/$division/$category/$courseID"
    }

    object ExamPreview : Destinations("exam_preview/{examId}") {
        fun route(examId: String) = "exam_preview/$examId"
    }

    object ExamUpload : Destinations("exam_upload")

    // Events
    object EventTracker : Destinations("event_tracker")
    object EventCreate : Destinations("event_create")
    object EventRegistration : Destinations("event_registration/{eventId}") {
        fun route(eventId: String) = "event_registration/$eventId"
    }
}