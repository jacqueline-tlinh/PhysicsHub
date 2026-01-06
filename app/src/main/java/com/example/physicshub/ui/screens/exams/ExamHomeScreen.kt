package com.example.physicshub.ui.screens.exams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.physicshub.data.local.RecentlyViewedEntity
import com.example.physicshub.data.model.ExamPaper
import com.example.physicshub.ui.navigation.Destinations
import com.example.physicshub.ui.screens.exams.archive.ExamArchiveViewModel

@Composable
fun ExamHomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ExamArchiveViewModel = viewModel()
) {
    val context = LocalContext.current
    val recentlyViewed by viewModel.recentlyViewed.collectAsState()
    var newestUploads by remember { mutableStateOf<List<ExamPaper>>(emptyList()) }

    // Initialize recently viewed and fetch newest uploads
    LaunchedEffect(Unit) {
        viewModel.initRecentlyViewed(context)
        viewModel.getNewestUploads(3) { uploads ->
            newestUploads = uploads
        }
    }

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
            subtitle = "Browse past exam papers by division and category",
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
            items = recentlyViewed.take(4),
            onItemClick = { item ->
                navController.navigate(Destinations.ExamPreview.route(item.examId))
            }
        )

        // Newest uploads
        SectionHeader("Newest Uploads")
        NewestUploadsSection(
            items = newestUploads,
            onItemClick = { exam ->
                navController.navigate(Destinations.ExamPreview.route(exam.id))
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
                text = "Can't find an exam you need?\nRequest it here.",
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
    items: List<RecentlyViewedEntity>,
    onItemClick: (RecentlyViewedEntity) -> Unit
) {
    if (items.isEmpty()) {
        EmptyStateCard("You haven't viewed any exam papers yet.\nStart browsing to see them here.")
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            RecentlyViewedCard(item, onItemClick)
        }
    }
}

@Composable
private fun NewestUploadsSection(
    items: List<ExamPaper>,
    onItemClick: (ExamPaper) -> Unit
) {
    if (items.isEmpty()) {
        EmptyStateCard("No uploads yet.\nBe the first to contribute!")
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { exam ->
            NewestUploadCard(exam, onItemClick)
        }
    }
}

@Composable
private fun RecentlyViewedCard(
    item: RecentlyViewedEntity,
    onClick: (RecentlyViewedEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(110.dp)
            .clickable { onClick(item) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.course,
                style = MaterialTheme.typography.titleSmall
            )
            Column {
                Text(
                    text = item.examType.uppercase(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Year ${item.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NewestUploadCard(
    exam: ExamPaper,
    onClick: (ExamPaper) -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(110.dp)
            .clickable { onClick(exam) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = exam.course,
                style = MaterialTheme.typography.titleSmall
            )
            Column {
                Text(
                    text = exam.examType.uppercase(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Year ${exam.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
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
                text = message,
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
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
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