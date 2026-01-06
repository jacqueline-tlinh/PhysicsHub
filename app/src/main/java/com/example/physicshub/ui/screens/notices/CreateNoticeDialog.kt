package com.example.physicshub.ui.screens.notices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoticeDialog(
    onDismiss: () -> Unit,
    onConfirm: (NoticeCategory, String, String, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(NoticeCategory.GENERAL) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val currentDate = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }

    val isTitleValid = title.isNotBlank()
    val isContentValid = content.isNotBlank()
    val isFormValid = isTitleValid && isContentValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create New Notice",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.translatedName(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        NoticeCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.translatedName()) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                // Title TextField
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Enter notice title") },
                    isError = title.isNotBlank() && !isTitleValid,
                    supportingText = {
                        if (title.isNotBlank() && !isTitleValid) {
                            Text("Title cannot be empty")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2
                )

                // Content TextField
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    placeholder = { Text("Enter notice content") },
                    isError = content.isNotBlank() && !isContentValid,
                    supportingText = {
                        if (content.isNotBlank() && !isContentValid) {
                            Text("Content cannot be empty")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    singleLine = false,
                    maxLines = 8
                )

                // Date Display
                Text(
                    text = "Date: $currentDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isFormValid) {
                        onConfirm(selectedCategory, title.trim(), content.trim(), currentDate)
                    }
                },
                enabled = isFormValid
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NoticeCategory.translatedName(): String {
    return when (this) {
        NoticeCategory.ACADEMIC_AFFAIRS -> "Academic Affairs"
        NoticeCategory.RESEARCH -> "Research"
        NoticeCategory.EVENTS -> "Events"
        NoticeCategory.GENERAL -> "General"
    }
}