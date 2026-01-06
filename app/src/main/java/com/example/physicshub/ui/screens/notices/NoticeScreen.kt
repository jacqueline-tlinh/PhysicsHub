package com.example.physicshub.ui.screens.notices

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.physicshub.ui.language.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(
    navController: NavController,
    viewModel: NoticeViewModel = viewModel()
) {
    val notices by viewModel.notices.collectAsState()
    val showUnreadOnly by viewModel.showUnreadOnly.collectAsState()
    val isCreatingNotice by viewModel.isCreatingNotice.collectAsState()
    val expandedNoticeIds by viewModel.expandedNoticeIds.collectAsState()

    val filteredNotices = remember(notices, showUnreadOnly) {
        if (showUnreadOnly) {
            notices.filter { !it.isRead }
        } else {
            notices
        }
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
                },
                actions = {
                    IconButton(onClick = { viewModel.showCreateNoticeDialog() }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create Notice"
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
            val totalCount = viewModel.getTotalCount()
            val unreadCount = viewModel.getUnreadCount()

            ReadUnreadToggle(
                showUnreadOnly = showUnreadOnly,
                onToggleChange = { viewModel.setUnreadFilter(it) },
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
                    NoticeExpandableCard(
                        notice = notice,
                        isExpanded = expandedNoticeIds.contains(notice.id),
                        onClick = {
                            viewModel.toggleNoticeExpansion(notice.id)
                        },
                        onToggleReadState = {
                            viewModel.toggleReadState(notice.id)
                        },
                        onDelete = {
                            viewModel.deleteNotice(notice.id)
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

    // Create Notice Dialog
    if (isCreatingNotice) {
        CreateNoticeDialog(
            onDismiss = { viewModel.hideCreateNoticeDialog() },
            onConfirm = { category, title, content, date ->
                viewModel.createNotice(category, title, content, date)
                viewModel.hideCreateNoticeDialog()
            }
        )
    }
}

@Composable
fun NoticeExpandableCard(
    notice: Notice,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onToggleReadState: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(16.dp),
        color = notice.category.backgroundColor,
        tonalElevation = if (isExpanded) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = notice.category.iconTint,
                        modifier = Modifier.size(24.dp)
                    )

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Content Preview (collapsed) or Full Content (expanded)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notice.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            // Date and Actions Row
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notice.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Action buttons (visible when expanded)
                AnimatedVisibility(visible = isExpanded) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Mark as read/unread button
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

                        // Delete button
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Notice") },
            text = { Text("Are you sure you want to delete this notice?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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