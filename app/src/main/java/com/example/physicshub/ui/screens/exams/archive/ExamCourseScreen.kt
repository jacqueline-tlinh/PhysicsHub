package com.example.physicshub.ui.screens.exams.archive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.ui.navigation.Destinations
import com.example.physicshub.ui.theme.PhysicsHubTheme

data class ExamFile(
    val id: String,
    val type: String,
    val semester: Int,
    val classId: String
)

private val mockExamFiles = listOf(
    ExamFile("1", "Midterm", 1, "PHY101-01"),
    ExamFile("2", "Final", 1, "PHY101-01"),
    ExamFile("3", "Midterm", 2, "PHY101-02"),
    ExamFile("4", "Final", 2, "PHY101-02"),
    ExamFile("5", "Final", 3, "PHY101-03")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamCourseScreen(
    navController: NavController,
    courseName: String
) {
    val selectedIds = remember { mutableStateListOf<String>() }

    val isSelectionMode = selectedIds.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isSelectionMode) {
                                selectedIds.clear()
                            } else {
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isSelectionMode) {

                        IconButton(
                            onClick = {
                                if (selectedIds.size == mockExamFiles.size) {
                                    selectedIds.clear()
                                } else {
                                    selectedIds.clear()
                                    selectedIds.addAll(mockExamFiles.map { it.id })
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelectAll,
                                contentDescription = "Select all"
                            )
                        }

                        IconButton(onClick = {
                            // TODO: batch download
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "Download")
                        }
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Course name (centered, prominent)
            Text(
                text = courseName,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = mockExamFiles,
                    key = { it.id }
                ) { exam ->
                    val isSelected = exam.id in selectedIds
                    ExamFileCard(
                        exam = exam,
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                if (isSelected) selectedIds.remove(exam.id)
                                else selectedIds.add(exam.id)
                            } else {
                                navController.navigate(
                                    Destinations.ExamPreview.route(
                                        courseName,
                                        exam.type,
                                        exam.semester,
                                        exam.classId
                                    )
                                )
                            }
                        },
                        onLongPress = {
                            if (!isSelectionMode) {
                                selectedIds.add(exam.id)
                            }
                        }

                    )
                }
            }
        }
    }
}

@Composable
fun ExamFileCard(
    exam: ExamFile,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected ->
                    MaterialTheme.colorScheme.primaryContainer
                isSelectionMode ->
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                else ->
                    MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // Exam type
                Text(
                    text = exam.type.uppercase(),
                    style = MaterialTheme.typography.labelLarge
                )

                Column {
                    Text(
                        text = "Semester ${exam.semester}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Class ${exam.classId}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Placeholder for overflow menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null, // handled by card click
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExamCourseScreenPreview() {
    PhysicsHubTheme {
        ExamCourseScreen(
            navController = rememberNavController(),
            courseName = "Physics I"
        )
    }
}
