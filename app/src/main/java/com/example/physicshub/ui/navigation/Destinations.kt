package com.example.physicshub.ui.navigation

sealed class Destinations(val route: String) {
    object Login : Destinations("login")
    object Home : Destinations("home")
    object Events : Destinations("events")
    object Notices : Destinations("notices")
    object Exams : Destinations("exams")

    object Booking: Destinations("booking")

    object ExamArchive : Destinations("exam_archive")

    object ExamDivision : Destinations("exam_division/{division}") {
        fun route(division: String) = "exam_division/$division"
    }

    object ExamCourse : Destinations(
        "exam_course/{division}/{subject}/{course}"
    ) {
        fun route(
            division: String,
            subject: String,
            course: String
        ) = "exam_course/$division/$subject/$course"
    }

    object ExamPreview : Destinations(
        "exam_preview/{course}/{type}/{semester}/{classId}"
    ) {
        fun route(
            course: String,
            type: String,
            semester: Int,
            classId: String
        ) = "exam_preview/$course/$type/$semester/$classId"
    }

    object ExamUpload : Destinations("exam_upload")
}
