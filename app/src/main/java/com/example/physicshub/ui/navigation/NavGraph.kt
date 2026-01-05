package com.example.physicshub.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.physicshub.ui.components.PhysicsHubScaffold
import com.example.physicshub.ui.screens.events.EventTrackerScreen
import com.example.physicshub.ui.screens.events.EventCreateScreen
import com.example.physicshub.ui.screens.events.EventRegistrationScreen
import com.example.physicshub.ui.screens.events.viewmodel.EventViewModel
import com.example.physicshub.ui.screens.exams.ExamHomeScreen
import com.example.physicshub.ui.screens.exams.archive.ExamArchiveRootScreen
import com.example.physicshub.ui.screens.exams.archive.ExamCategoryScreen
import com.example.physicshub.ui.screens.exams.archive.ExamCourseScreen
import com.example.physicshub.ui.screens.exams.archive.ExamFilesScreen
import com.example.physicshub.ui.screens.exams.archive.ExamPreviewScreen
import com.example.physicshub.ui.screens.exams.upload.ExamUploadScreen
import com.example.physicshub.ui.screens.home.HomeScreen
import com.example.physicshub.ui.screens.login.LoginScreen
import com.example.physicshub.ui.screens.notices.NoticeScreen

@Composable
fun PhysicsHubNavGraph() {
    val navController = rememberNavController()

    val eventViewModel: EventViewModel = viewModel()

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
                EventTrackerScreen(navController, eventViewModel)
            }

            composable(Destinations.EventTracker.route) {
                EventTrackerScreen(navController, eventViewModel)
            }

            composable(Destinations.EventCreate.route) {
                EventCreateScreen(navController, eventViewModel)
            }

            composable(
                route = Destinations.EventRegistration.route,
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventRegistrationScreen(
                    navController = navController,
                    eventId = eventId,
                    viewModel = eventViewModel
                )
            }

            composable(Destinations.Notices.route) {
                NoticeScreen(navController)
            }

            composable(Destinations.Exams.route) {
                ExamHomeScreen(navController)
            }

            composable(Destinations.ExamArchive.route) {
                ExamArchiveRootScreen(navController)
            }

            composable(
                route = Destinations.ExamCategory.route,
                arguments = listOf(
                    navArgument("division") { type = NavType.StringType }
                )
            ) { entry ->
                ExamCategoryScreen(
                    navController = navController,
                    division = entry.arguments?.getString("division").orEmpty()
                )
            }

            composable(
                route = Destinations.ExamCourse.route,
                arguments = listOf(
                    navArgument("division") { type = NavType.StringType },
                    navArgument("category") { type = NavType.StringType }
                )
            ) { entry ->
                ExamCourseScreen(
                    navController = navController,
                    courseName = entry.arguments
                        ?.getString("category")
                        .orEmpty()
                )
            }

            composable(
                route = Destinations.ExamFiles.route,
                arguments = listOf(
                    navArgument("division") { type = NavType.StringType },
                    navArgument("category") { type = NavType.StringType },
                    navArgument("course") { type = NavType.StringType }
                )
            ) { entry ->
                ExamFilesScreen(
                    navController = navController,
                    division = entry.arguments?.getString("division").orEmpty(),
                    category = entry.arguments?.getString("category").orEmpty(),
                    course = entry.arguments?.getString("course").orEmpty()
                )
            }

            composable(
                route = Destinations.ExamPreview.route,
                arguments = listOf(
                    navArgument("examId") { type = NavType.StringType }
                )
            ) { entry ->
                ExamPreviewScreen(
                    navController = navController,
                    examId = entry.arguments?.getString("examId").orEmpty()
                )
            }

            composable(Destinations.ExamUpload.route) {
                ExamUploadScreen(navController)
            }

            composable(Destinations.ExamUpload.route) {
                ExamUploadScreen(navController)
            }
        }
    }
}