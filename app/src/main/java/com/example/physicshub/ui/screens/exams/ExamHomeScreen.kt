package com.example.physicshub.ui.screens.exams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.ui.components.PhysicsHubScaffold
import com.example.physicshub.ui.navigation.Destinations
import com.example.physicshub.ui.theme.PhysicsHubTheme

data class ExamPreviewItem(
    val course: String,
    val examType: String,
    val semester: Int
)

private val mockRecentlyViewed = emptyList<ExamPreviewItem>()

private val mockNewestUploads = listOf(
    ExamPreviewItem("Calculus I", "Final", 2),
    ExamPreviewItem("Physics I", "Midterm", 1),
    ExamPreviewItem("Linear Algebra", "Final", 2)
)

@Composable
fun ExamHomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Primary actions
        ExamFeatureCard(
            icon = Icons.Default.Description,
            title = "Exam Archive",
            subtitle = "Browse past exam papers by subject and semester",
            containerColor = Color(0xFFE3F2FD),
            onClick = { navController.navigate(Destinations.ExamArchive.route) }
        )

        ExamFeatureCard(
            icon = Icons.Default.FileUpload,
            title = "Upload Papers",
            subtitle = "Contribute exam papers for others to review",
            containerColor = Color(0xFFE8F5E9),
            onClick = { navController.navigate(Destinations.ExamUpload.route) }
        )

        // Recently viewed
        SectionHeader("Recently Viewed")
        RecentlyViewedSection(
            items = mockRecentlyViewed,
            onItemClick = {
                // TODO: navigate to ExamPreviewScreen
            }
        )

        // Newest uploads
        SectionHeader("Newest Uploads")
        RecentlyViewedSection(
            items = mockNewestUploads,
            onItemClick = {
                // TODO: navigate to ExamPreviewScreen
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom CTA
        Button(
            onClick = {
                // TODO: suggestion/request navigation
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Can’t find an exam you need? \nRequest it here.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun RecentlyViewedSection(
    items: List<ExamPreviewItem>,
    onItemClick: (ExamPreviewItem) -> Unit
) {
    if (items.isEmpty()) {
        EmptyStateCard()
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.take(4)) { item ->
            ExamPreviewCard(item, onItemClick)
        }
    }
}

@Composable
private fun ExamPreviewCard(
    item: ExamPreviewItem,
    onClick: (ExamPreviewItem) -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(120.dp)
            .clickable { onClick(item) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item.course, style = MaterialTheme.typography.titleSmall)
            Text(item.examType, style = MaterialTheme.typography.bodySmall)
            Text("Semester ${item.semester}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF2F2F2)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "You haven’t viewed any exam papers yet.\nStart browsing to see them here.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun ExamFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    large: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (large) 110.dp else 72.dp)
            .then(
                if (enabled && onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // subtle lift
    ) {
        Row(
            modifier = Modifier.padding(if (large) 20.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(if (large) 40.dp else 32.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = title,
                    style = if (large)
                        MaterialTheme.typography.headlineSmall
                    else
                        MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExamHomePreview() {
    PhysicsHubTheme {
        val navController = rememberNavController()
        PhysicsHubScaffold(navController = navController) { padding ->
            ExamHomeScreen(
                navController = navController
            )
        }
    }

}
