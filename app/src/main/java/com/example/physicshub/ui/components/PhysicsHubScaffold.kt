package com.example.physicshub.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.ui.navigation.BottomNavItem
import com.example.physicshub.ui.theme.PhysicsHubTheme

@Composable
fun PhysicsHubScaffold(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("events", "Events", Icons.Default.Event),
        BottomNavItem("notices", "Notices", Icons.Default.Notifications),
        BottomNavItem("exams", "Exams", Icons.Default.Description)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo("home")
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)
                        },
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
        },
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun ScaffoldPreview() {
    PhysicsHubTheme {
        val navController = rememberNavController()
        PhysicsHubScaffold(navController) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                Text("Scaffold Preview")
            }
        }
    }
}
