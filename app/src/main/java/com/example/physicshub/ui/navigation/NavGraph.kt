package com.example.physicshub.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.physicshub.ui.components.PhysicsHubScaffold
import com.example.physicshub.ui.screens.bookiing.BookingScreen
import com.example.physicshub.ui.screens.events.EventScreen
import com.example.physicshub.ui.screens.exams.ExamHomeScreen
import com.example.physicshub.ui.screens.exams.archive.ExamArchiveRootScreen
import com.example.physicshub.ui.screens.exams.archive.ExamCourseScreen
import com.example.physicshub.ui.screens.exams.archive.ExamPreviewScreen
import com.example.physicshub.ui.screens.exams.archive.ExamSubjectScreen
import com.example.physicshub.ui.screens.home.HomeScreen
import com.example.physicshub.ui.screens.login.LoginScreen
import com.example.physicshub.ui.screens.notices.NoticeScreen

@Composable
fun PhysicsHubNavGraph() {
    val navController = rememberNavController()

    PhysicsHubScaffold(navController = navController) { padding ->

        NavHost(
            navController = navController,
            startDestination = Destinations.Login.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Destinations.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Destinations.Home.route) {
                            popUpTo(Destinations.Login.route) { inclusive = true }
                        }
                    }
                )
            }

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
                ExamHomeScreen(navController)
            }

            composable(Destinations.Booking.route) {
                BookingScreen(navController)
            }

            composable(Destinations.ExamArchive.route) {
                ExamArchiveRootScreen(navController)
            }

            composable(
                Destinations.ExamDivision.route,
                arguments = listOf(
                    navArgument("division") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->
                ExamSubjectScreen(
                    navController = navController,
                    title = entry.arguments!!
                        .getString("division")!!
                )
            }

            composable(
                Destinations.ExamCourse.route,
                arguments = listOf(
                    navArgument("division") { type = NavType.StringType },
                    navArgument("subject") { type = NavType.StringType },
                    navArgument("course") { type = NavType.StringType }
                )
            ) { entry ->
                ExamCourseScreen(
                    navController = navController,
                    courseName = entry.arguments!!
                        .getString("course")!!
                )
            }

            composable(
                route = Destinations.ExamPreview.route,
                arguments = listOf(
                    navArgument("course") { type = NavType.StringType },
                    navArgument("type") { type = NavType.StringType },
                    navArgument("semester") { type = NavType.IntType },
                    navArgument("classId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                ExamPreviewScreen(
                    navController = navController,
                    courseName = backStackEntry.arguments?.getString("course") ?: "",
                    examType = backStackEntry.arguments?.getString("type") ?: "",
                    semester = backStackEntry.arguments?.getInt("semester") ?: 0,
                    classId = backStackEntry.arguments?.getString("classId") ?: ""
                )
            }

        }
    }
}