package com.example.physicshub.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.physicshub.ui.components.PhysicsHubScaffold
import com.example.physicshub.ui.screens.*
import com.example.physicshub.ui.screens.events.EventScreen
import com.example.physicshub.ui.screens.exams.ExamArchiveScreen
import com.example.physicshub.ui.screens.home.HomeScreen
import com.example.physicshub.ui.screens.login.LoginScreen
import com.example.physicshub.ui.screens.notices.NoticeScreen

@Composable
fun PhysicsHubNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.Login.route
    ) {
        composable(Destinations.Login.route) {
            LoginScreen(navController)
        }

        composable("main") {
            PhysicsHubScaffold(navController = navController) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Destinations.Home.route,
                    modifier = Modifier.padding(padding)
                ) {
                    composable(Destinations.Home.route) {
                        HomeScreen(navController)
                    }
                    composable(Destinations.Events.route) {
                        EventScreen(navController)
                    }
                    composable(Destinations.Notices.route) {
                        NoticeScreen(navController)
                    }
                    composable(Destinations.Exams.route) {
                        ExamArchiveScreen(navController)
                    }
                }
            }
        }
    }
}

