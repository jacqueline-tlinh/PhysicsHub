package com.example.physicshub.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.ui.navigation.Destinations
import com.example.physicshub.ui.theme.PhysicsHubTheme

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeCard(
            title = "Upcoming Events",
            subtitle = "2 events this week",
            onClick = { navController.navigate(Destinations.Events.route) }
        )

        HomeCard(
            title = "Notices",
            subtitle = "3 unread announcements",
            onClick = { navController.navigate(Destinations.Notices.route) }
        )

        HomeCard(
            title = "Exam Archive",
            subtitle = "Past papers available",
            onClick = { navController.navigate(Destinations.Exams.route) }
        )
    }
}

@Composable
fun HomeCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PhysicsHubTheme {
        HomeScreen(navController = rememberNavController())
    }
}
