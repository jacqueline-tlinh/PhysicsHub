package com.example.physicshub.ui.screens.notices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.physicshub.ui.language.Strings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { NoticeRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    var showUnreadOnly by remember { mutableStateOf(false) }

    val cachedNotices by repository.cachedNotices.collectAsState(initial = mockNotices)
    val readIds by repository.readNoticeIds.collectAsState(initial = emptySet())

    val notices = cachedNotices.map { notice ->
        notice.copy(isRead = readIds.contains(notice.id))
    }

    val filteredNotices = if (showUnreadOnly) {
        notices.filter { !it.isRead }
    } else {
        notices
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.noticeBoard,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Strings.back
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val totalCount = notices.size
            val unreadCount = notices.count { !it.isRead }

            ReadUnreadToggle(
                showUnreadOnly = showUnreadOnly,
                onToggleChange = { showUnreadOnly = it },
                totalCount = totalCount,
                unreadCount = unreadCount,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredNotices, key = { it.id }) { notice ->
                    NoticeListCard(
                        notice = notice,
                        onClick = {
                            // Future: navigate to notice details
                        },
                        onToggleReadState = {
                            scope.launch {
                                if (notice.isRead) {
                                    repository.markAsUnread(notice.id)
                                } else {
                                    repository.markAsRead(notice.id)
                                }
                            }
                        }
                    )
                }

                if (filteredNotices.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (showUnreadOnly) Strings.noUnreadNotices else Strings.noNotices,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return if (count > 99) "99+" else count.toString()
}

@Composable
private fun NoticeCategory.translatedName(): String {
    return when (this) {
        NoticeCategory.ACADEMIC_AFFAIRS -> Strings.academicAffairs
        NoticeCategory.RESEARCH -> Strings.research
        NoticeCategory.EVENTS -> Strings.events
        NoticeCategory.GENERAL -> Strings.general
    }
}

@Composable
fun ReadUnreadToggle(
    showUnreadOnly: Boolean,
    onToggleChange: (Boolean) -> Unit,
    totalCount: Int,
    unreadCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = !showUnreadOnly,
            onClick = { onToggleChange(false) },
            label = { Text("${Strings.all} (${formatCount(totalCount)})") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        FilterChip(
            selected = showUnreadOnly,
            onClick = { onToggleChange(true) },
            label = { Text("${Strings.unread} (${formatCount(unreadCount)})") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Composable
fun NoticeListCard(
    notice: Notice,
    onClick: () -> Unit,
    onToggleReadState: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = notice.category.backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = notice.category.translatedName(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!notice.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Text(
                    text = notice.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (!notice.isRead) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = notice.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = notice.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = notice.category.iconTint,
                    modifier = Modifier.size(24.dp)
                )

                IconButton(
                    onClick = onToggleReadState,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (notice.isRead)
                            Icons.Default.MarkEmailUnread
                        else
                            Icons.Default.MarkEmailRead,
                        contentDescription = if (notice.isRead) "Mark as unread" else "Mark as read",
                        tint = if (notice.isRead)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}