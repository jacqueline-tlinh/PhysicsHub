package com.example.physicshub.ui.screens.exams.archive

import android.R.attr.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.ui.navigation.Destinations
import com.example.physicshub.ui.theme.PhysicsHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamArchiveRootScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Archive") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
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
                    "Miscellaneous",
                ),
                onItemClick = { division ->
                    navController.navigate(
                        Destinations.ExamDivision.route(division)
                    )
                }
            )

            ExamSection(
                title = "Advanced",
                items = listOf(
                    "Physics",
                    "Engineering",
                ),
                onItemClick = { division ->
                    navController.navigate(
                        Destinations.ExamDivision.route(division)
                    )
                }
            )
        }
    }
}

@Composable
private fun ExamSection(
    title: String,
    items: List<String>,
    onItemClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )

        items.forEach { item ->
            ExamCategoryCard(
                title = item,
                containerColor = Color(0xFFE3F2F0),
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun ExamCategoryCard(
    title: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
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

