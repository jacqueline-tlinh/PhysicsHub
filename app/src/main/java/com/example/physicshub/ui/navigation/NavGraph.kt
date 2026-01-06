package com.example.physicshub.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.physicshub.data.repository.AuthRepository
import com.example.physicshub.ui.components.PhysicsHubScaffold
import com.example.physicshub.ui.screens.events.EventCreateScreen
import com.example.physicshub.ui.screens.events.EventRegistrationScreen
import com.example.physicshub.ui.screens.events.EventTrackerScreen
import com.example.physicshub.ui.screens.events.viewmodel.EventViewModel
import com.example.physicshub.ui.screens.exams.ExamHomeScreen
import com.example.physicshub.ui.screens.exams.archive.ExamArchiveRootScreen
import com.example.physicshub.ui.screens.exams.archive.ExamCategoryScreen
import com.example.physicshub.ui.screens.exams.archive.ExamCourseListScreen
import com.example.physicshub.ui.screens.exams.archive.ExamFilesScreen
import com.example.physicshub.ui.screens.exams.archive.ExamPreviewScreen
import com.example.physicshub.ui.screens.exams.upload.ExamUploadScreen
import com.example.physicshub.ui.screens.home.HomeScreen
import com.example.physicshub.ui.screens.login.LoginScreen
import com.example.physicshub.ui.screens.notices.NoticeScreen
import com.example.physicshub.ui.screens.notices.NoticeViewModel
import com.example.physicshub.ui.theme.ThemeViewModel

@Composable
fun PhysicsHubNavGraph(
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authRepository = remember { AuthRepository.getInstance(context) }

    // Check if user is logged in
    val isLoggedIn by authRepository.isLoggedIn().collectAsState(initial = null)

    // Show loading screen while checking auth state
    if (isLoggedIn == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Determine start destination based on login state
    val startDestination = if (isLoggedIn == true) {
        Destinations.Home.route
    } else {
        Destinations.Login.route
    }

    // ✅ SHARED VIEWMODELS - Tạo ở level NavGraph để share giữa các screens
    val sharedEventViewModel: EventViewModel = viewModel()
    val sharedNoticeViewModel: NoticeViewModel = viewModel()

    PhysicsHubScaffold(navController = navController) { padding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {

            // ============================================
            // AUTHENTICATION
            // ============================================
            composable(Destinations.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Destinations.Home.route) {
                            popUpTo(Destinations.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // ============================================
            // HOME SCREEN
            // ============================================
            composable(Destinations.Home.route) {
                HomeScreen(
                    navController = navController,
                    sharedEventViewModel = sharedEventViewModel,  // ✅ Truyền shared ViewModel
                    themeViewModel = themeViewModel,
                    noticeViewModel = sharedNoticeViewModel
                )
            }

            // ============================================
            // EVENT SCREENS - Tất cả dùng chung sharedEventViewModel
            // ============================================
            composable(Destinations.Events.route) {
                EventTrackerScreen(
                    navController = navController,
                    viewModel = sharedEventViewModel  // ✅ Shared ViewModel
                )
            }

            composable(Destinations.EventTracker.route) {
                EventTrackerScreen(
                    navController = navController,
                    viewModel = sharedEventViewModel  // ✅ Shared ViewModel
                )
            }

            composable(Destinations.EventCreate.route) {
                EventCreateScreen(
                    navController = navController,
                    viewModel = sharedEventViewModel  // ✅ Shared ViewModel
                )
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
                    viewModel = sharedEventViewModel  // ✅ Shared ViewModel
                )
            }

            // ============================================
            // NOTICE SCREEN
            // ============================================
            composable(Destinations.Notices.route) {
                NoticeScreen(
                    navController = navController,
                    viewModel = sharedNoticeViewModel  // ✅ Shared ViewModel
                )
            }

            // ============================================
            // EXAM SCREENS
            // ============================================
            composable(Destinations.Exams.route) {
                ExamHomeScreen(navController = navController)
            }

            composable(Destinations.ExamArchive.route) {
                ExamArchiveRootScreen(navController = navController)
            }

            composable(
                route = Destinations.ExamCategory.route,
                arguments = listOf(
                    navArgument("division") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val division = backStackEntry.arguments?.getString("division") ?: ""
                ExamCategoryScreen(
                    navController = navController,
                    division = division
                )
            }

            composable(
                route = Destinations.ExamCourse.route,
                arguments = listOf(
                    navArgument("division") { type = NavType.StringType },
                    navArgument("category") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val division = backStackEntry.arguments?.getString("division") ?: ""
                val category = backStackEntry.arguments?.getString("category") ?: ""
                ExamCourseListScreen(
                    navController = navController,
                    division = division,
                    category = category
                )
            }

            composable(
                route = Destinations.ExamFiles.route,
                arguments = listOf(
                    navArgument("division") { type = NavType.StringType },
                    navArgument("category") { type = NavType.StringType },
                    navArgument("courseID") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val division = backStackEntry.arguments?.getString("division") ?: ""
                val category = backStackEntry.arguments?.getString("category") ?: ""
                val courseID = backStackEntry.arguments?.getString("courseID") ?: ""
                ExamFilesScreen(
                    navController = navController,
                    division = division,
                    category = category,
                    courseID = courseID
                )
            }

            composable(
                route = Destinations.ExamPreview.route,
                arguments = listOf(
                    navArgument("examId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val examId = backStackEntry.arguments?.getString("examId") ?: ""
                ExamPreviewScreen(
                    navController = navController,
                    examId = examId
                )
            }

            composable(Destinations.ExamUpload.route) {
                ExamUploadScreen(navController = navController)
            }
        }
    }
}