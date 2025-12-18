package com.example.physicshub.ui.navigation

sealed class Destinations(val route: String) {
    object Login : Destinations("login")
    object Home : Destinations("home")
    object Events : Destinations("events")
    object Notices : Destinations("notices")
    object Exams : Destinations("exams")
}
