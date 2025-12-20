package com.example.physicshub.ui.screens.exams.archive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.ui.theme.PhysicsHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamArchiveRootScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Archive") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search exams") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExamSection(
                title = "Fundamentals",
                items = listOf(
                    "Natural Sciences",
                    "Social Sciences",
                    "Miscellaneous"
                )
            )

            ExamSection(
                title = "Advanced",
                items = listOf(
                    "Physics",
                    "Engineering"
                )
            )
        }
    }
}

@Composable
private fun ExamSection(
    title: String,
    items: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 240.dp)
        ) {
            items(items) { item ->
                ExamCategoryCard(title = item)
            }
        }
    }
}

@Composable
private fun ExamCategoryCard(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable { /* later navigation */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExamArchiveRootPreview() {
    PhysicsHubTheme {
        ExamArchiveRootScreen(
            navController = rememberNavController()
        )
    }
}

