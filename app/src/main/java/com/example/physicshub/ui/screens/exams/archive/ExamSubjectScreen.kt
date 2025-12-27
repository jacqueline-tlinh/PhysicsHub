package com.example.physicshub.ui.screens.exams.archive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.ui.navigation.Destinations
import com.example.physicshub.ui.theme.PhysicsHubTheme

// ---- Mock data (replace later with database) ----

private val mockSubjects = mapOf(
    "Mathematics" to listOf(
        "Calculus I",
        "Calculus II",
        "Linear Algebra"
    ),
    "Physics" to listOf(
        "Classical Mechanics",
        "Electromagnetism",
        "Thermodynamics"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSubjectScreen(
    navController: NavController,
    title: String = "Natural Sciences"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search courses") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )
            }

            mockSubjects.forEach { (subject, courses) ->

                item {
                    SubjectHeader(
                        title = subject,
                        onClick = {
                            navController.navigate(
                                Destinations.ExamCourse.route(
                                    title,
                                    subject,
                                    "ALL"
                                )
                            )
                        }
                    )
                }

                items(courses) { course ->
                    CourseItem(
                        name = course,
                        onClick = {
                            navController.navigate(
                                Destinations.ExamCourse.route(
                                    title,
                                    subject,
                                    course
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectHeader(
    title: String,
    onClick: () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun CourseItem(
    name: String,
    onClick: () -> Unit
) {
    val pastelGreen = Color(0xFF277795)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = pastelGreen.copy(alpha = 0.10f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Course name
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Forward indicator
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExamSubjectPreview() {
    PhysicsHubTheme {
        ExamSubjectScreen(navController = rememberNavController())
    }
}
