package com.example.physicshub.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavDestination(
        route = "home",
        label = "Home",
        icon = Icons.Default.Home
    )

    object Events : BottomNavDestination(
        route = "events",
        label = "Events",
        icon = Icons.Default.Event
    )

    object Notices : BottomNavDestination(
        route = "notices",
        label = "Notices",
        icon = Icons.Default.Campaign
    )

    object Exam : BottomNavDestination(
        route = "exam",
        label = "Exam",
        icon = Icons.Default.Description
    )
}

val bottomNavItems = listOf(
    BottomNavDestination.Home,
    BottomNavDestination.Events,
    BottomNavDestination.Notices,
    BottomNavDestination.Exam
)
