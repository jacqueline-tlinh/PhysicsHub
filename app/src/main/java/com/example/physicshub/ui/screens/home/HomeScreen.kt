package com.example.physicshub.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.physicshub.data.model.ExamPaper
import com.example.physicshub.ui.components.PhysicsHubScaffold
import com.example.physicshub.ui.language.Language
import com.example.physicshub.ui.language.LanguageManager
import com.example.physicshub.ui.language.Strings
import com.example.physicshub.ui.navigation.Destinations
import com.example.physicshub.ui.screens.exams.archive.ExamArchiveViewModel
import com.example.physicshub.ui.screens.notices.Notice
import com.example.physicshub.ui.screens.notices.NoticeCategory
import com.example.physicshub.ui.screens.notices.NoticeRepository
import com.example.physicshub.ui.screens.notices.mockNotices
import com.example.physicshub.ui.theme.PhysicsHubTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    examViewModel: ExamArchiveViewModel = viewModel()
) {
    val context = LocalContext.current
    val repository = remember { NoticeRepository.getInstance(context) }
    val languageManager = remember { LanguageManager.getInstance(context) }
    val scope = rememberCoroutineScope()

    val cachedNotices by repository.cachedNotices.collectAsState(initial = mockNotices)
    val readIds by repository.readNoticeIds.collectAsState(initial = emptySet())
    val currentLanguage by languageManager.currentLanguage.collectAsState(initial = Language.ENGLISH)

    val notices = cachedNotices.map { notice ->
        notice.copy(isRead = readIds.contains(notice.id))
    }

    var newestUploads by remember { mutableStateOf<List<ExamPaper>>(emptyList()) }

    LaunchedEffect(Unit) {
        examViewModel.getNewestUploads(3) { uploads ->
            newestUploads = uploads
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                currentLanguage = currentLanguage,
                onLanguageToggle = {
                    scope.launch {
                        languageManager.toggleLanguage()
                    }
                },
                onNotificationClick = {
                    navController.navigate("notifications")
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            SectionPlaceholder(
                title = Strings.upcomingEvent,
                onClick = { navController.navigate("events") }
            )

            NoticeBoardSection(
                notices = notices.take(4),
                onViewMoreClick = { navController.navigate("notices") },
                onNoticeClick = { navController.navigate("notices") }
            )

            NewestExamUploadsSection(
                uploads = newestUploads,
                onViewMoreClick = { navController.navigate(Destinations.ExamArchive.route) },
                onExamClick = { exam ->
                    navController.navigate(Destinations.ExamPreview.route(exam.id))
                }
            )
        }
    }
}

@Composable
fun HomeTopBar(
    currentLanguage: Language,
    onLanguageToggle: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = Strings.hello,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "User",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LanguageToggle(
                currentLanguage = currentLanguage,
                onToggle = onLanguageToggle
            )

            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = Strings.notifications
                )
            }
        }
    }
}

@Composable
fun LanguageToggle(
    currentLanguage: Language,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanguageOption(
                text = "EN",
                isSelected = currentLanguage == Language.ENGLISH
            )
            LanguageOption(
                text = "VN",
                isSelected = currentLanguage == Language.VIETNAMESE
            )
        }
    }
}

@Composable
private fun LanguageOption(
    text: String,
    isSelected: Boolean
) {
    Surface(
        modifier = Modifier.padding(2.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionPlaceholder(
    title: String,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = Strings.placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NoticeBoardSection(
    notices: List<Notice>,
    onViewMoreClick: () -> Unit,
    onNoticeClick: (Notice) -> Unit,
    autoScrollIntervalMs: Long = 30000L
) {
    val pagerState = rememberPagerState(pageCount = { notices.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoScrollIntervalMs)
            val nextPage = (pagerState.currentPage + 1) % notices.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Strings.noticeBoard,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = onViewMoreClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = Strings.viewMore,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(end = 56.dp),
            pageSpacing = 8.dp
        ) { page ->
            NoticeSliderCard(
                notice = notices[page],
                onClick = { onNoticeClick(notices[page]) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        PagerIndicator(
            pageCount = notices.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .width(if (isSelected) 24.dp else 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isSelected) Color.Gray else Color.LightGray
                    )
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun NoticeCategory.translatedName(): String {
    return when (this) {
        NoticeCategory.ACADEMIC_AFFAIRS -> Strings.academicAffairs
        NoticeCategory.RESEARCH -> Strings.research
        NoticeCategory.EVENTS -> Strings.events
        NoticeCategory.GENERAL -> Strings.general
    }
}

@Composable
fun NoticeSliderCard(
    notice: Notice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = notice.category.backgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = notice.category.translatedName(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Text(
                    text = notice.content,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
            }


            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = notice.category.iconTint,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
            )
        }
    }
}
@Composable
fun NewestExamUploadsSection(
    uploads: List<ExamPaper>,
    onViewMoreClick: () -> Unit,
    onExamClick: (ExamPaper) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Strings.examArchive,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = onViewMoreClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = Strings.viewMore,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uploads.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF2F2F2)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No uploads yet.\nBe the first to contribute!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uploads.forEach { exam ->
                    ExamUploadCard(
                        exam = exam,
                        onClick = { onExamClick(exam) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExamUploadCard(
    exam: ExamPaper,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE8F5E9)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.course,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${exam.examType.uppercase()} • Year ${exam.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = exam.fileType.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeWithBottomNavPreview() {
    PhysicsHubTheme {
        val navController = rememberNavController()
        PhysicsHubScaffold(navController = navController) { padding ->
            HomeScreen(
                navController = navController
            )
        }
    }
}
