package com.example.physicshub.ui.screens.exams.upload

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.physicshub.data.model.ExamType
import com.example.physicshub.data.model.FileType
import com.example.physicshub.util.FileValidation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamUploadScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ExamUploadViewModel = viewModel()

    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedFileNames by remember { mutableStateOf<List<String>>(emptyList()) }

    val metadata by viewModel.metadata.collectAsState()
    val loadingMetadata by viewModel.loadingMetadata.collectAsState()

    val uploadState by viewModel.uploadState.collectAsState()
    val canUpload by viewModel.canUpload.collectAsState()

    val divisions by viewModel.divisions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val courses by viewModel.courses.collectAsState()

    val selectedDivision by viewModel.division.collectAsState()
    val selectedCategory by viewModel.category.collectAsState()
    val selectedCourseName by viewModel.courseName.collectAsState()  // Changed from course to courseName
    val selectedCourseID by viewModel.courseID.collectAsState()      // Added courseID
    val selectedExamType by viewModel.examType.collectAsState()
    val selectedYear by viewModel.year.collectAsState()

    var divisionExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var courseExpanded by remember { mutableStateOf(false) }
    var examTypeExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val resolver = context.contentResolver

    // PDF Picker (single file)
    val pdfPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                if (FileValidation.isValidFile(context, uri)) {
                    selectedFiles = listOf(uri)
                    val cursor = resolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (displayNameIndex != -1) {
                                selectedFileNames = listOf(it.getString(displayNameIndex))
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Invalid file type or file exceeds 10MB",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    // Image Picker (multiple files)
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->
            if (uris.isNotEmpty()) {
                val validUris = uris.filter { FileValidation.isValidFile(context, it) }
                if (validUris.isEmpty()) {
                    Toast.makeText(
                        context,
                        "No valid images selected (max 10MB each)",
                        Toast.LENGTH_LONG
                    ).show()
                    return@rememberLauncherForActivityResult
                }

                selectedFiles = validUris
                selectedFileNames = validUris.mapNotNull { uri ->
                    val cursor = resolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (displayNameIndex != -1) {
                                it.getString(displayNameIndex)
                            } else null
                        } else null
                    }
                }

                if (validUris.size < uris.size) {
                    Toast.makeText(
                        context,
                        "Some files were invalid and skipped",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    // ---------- UPLOAD RESULT HANDLING ----------
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is ExamUploadViewModel.UploadState.Success -> {
                Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
                viewModel.resetUploadState()
                navController.popBackStack()
            }

            is ExamUploadViewModel.UploadState.Error -> {
                val errorMsg = (uploadState as ExamUploadViewModel.UploadState.Error).message
                Toast.makeText(
                    context,
                    errorMsg,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetUploadState()
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Exam Paper") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (loadingMetadata) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Division Dropdown
                ExposedDropdownMenuBox(
                    expanded = divisionExpanded,
                    onExpandedChange = { divisionExpanded = !divisionExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDivision ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Division") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = divisionExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = divisionExpanded,
                        onDismissRequest = { divisionExpanded = false }
                    ) {
                        divisions.forEach { division ->
                            DropdownMenuItem(
                                text = { Text(division) },
                                onClick = {
                                    viewModel.selectDivision(division)
                                    divisionExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Dropdown (enabled only when division is selected)
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = {
                        if (selectedDivision != null) {
                            categoryExpanded = !categoryExpanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = selectedCategory ?: "",
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedDivision != null,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    viewModel.selectCategory(category)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Course Dropdown (enabled only when category is selected)
                ExposedDropdownMenuBox(
                    expanded = courseExpanded,
                    onExpandedChange = {
                        if (selectedCategory != null) {
                            courseExpanded = !courseExpanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = selectedCourseName ?: "",  // Changed from selectedCourse
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedCategory != null,
                        label = { Text("Course") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = courseExpanded,
                        onDismissRequest = { courseExpanded = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course) },
                                onClick = {
                                    viewModel.selectCourse(course)
                                    courseExpanded = false
                                }
                            )
                        }
                    }
                }

                // Exam Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = examTypeExpanded,
                    onExpandedChange = { examTypeExpanded = !examTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedExamType?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Exam Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = examTypeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = examTypeExpanded,
                        onDismissRequest = { examTypeExpanded = false }
                    ) {
                        ExamType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    viewModel.selectExamType(type)
                                    examTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Year Dropdown
                ExposedDropdownMenuBox(
                    expanded = yearExpanded,
                    onExpandedChange = { yearExpanded = !yearExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedYear?.toString() ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Year") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = yearExpanded,
                        onDismissRequest = { yearExpanded = false }
                    ) {
                        (2020..2026).forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = {
                                    viewModel.selectYear(year)
                                    yearExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // File Selection Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            pdfPickerLauncher.launch(arrayOf("application/pdf"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Select PDF")
                    }

                    OutlinedButton(
                        onClick = {
                            imagePickerLauncher.launch(
                                arrayOf("image/jpeg", "image/png")
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Select Images")
                    }
                }

                // Display selected files
                if (selectedFileNames.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Selected files (${selectedFileNames.size}):",
                                style = MaterialTheme.typography.labelMedium
                            )
                            selectedFileNames.forEach { name ->
                                Text(
                                    text = "• $name",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                // Upload Button
                Button(
                    enabled = selectedFiles.isNotEmpty() && canUpload && uploadState !is ExamUploadViewModel.UploadState.Uploading,
                    onClick = {
                        println("🚀 Upload button clicked")
                        println("📋 Selected files: ${selectedFiles.size}")
                        println("📊 Can upload: $canUpload")
                        println("🏷️ Division: $selectedDivision")
                        println("🏷️ Category: $selectedCategory")
                        println("🏷️ Course Name: $selectedCourseName")
                        println("🏷️ Course ID: $selectedCourseID")
                        println("🏷️ Exam Type: $selectedExamType")
                        println("🏷️ Year: $selectedYear")

                        val resolver = context.contentResolver

                        // Get file type from first file
                        val firstMimeType = resolver.getType(selectedFiles.first())
                        println("🔎 MIME type: $firstMimeType")
                        val fileType = firstMimeType?.let { FileValidation.getFileType(it) } ?: FileType.PDF
                        println("📄 File type: $fileType")

                        if (selectedFiles.size == 1) {
                            // Single file upload
                            println("📤 Starting single file upload")
                            val file = selectedFiles.first()
                            val fileSize = resolver.openFileDescriptor(file, "r")?.statSize ?: 0L
                            println("💾 File size: $fileSize bytes")

                            viewModel.uploadExam(
                                fileUri = file,
                                fileType = fileType,
                                fileSize = fileSize,
                                uploadedBy = "demoUser",
                                role = "student"
                            )
                        } else {
                            // Multiple files upload
                            println("📤 Starting multiple files upload")
                            val fileSizes = selectedFiles.map { uri ->
                                resolver.openFileDescriptor(uri, "r")?.statSize ?: 0L
                            }
                            println("💾 Total files: ${fileSizes.size}, sizes: $fileSizes")

                            viewModel.uploadMultipleExams(
                                fileUris = selectedFiles,
                                fileType = fileType,
                                fileSizes = fileSizes,
                                uploadedBy = "demoUser",
                                role = "student"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when (uploadState) {
                            ExamUploadViewModel.UploadState.Uploading -> "Uploading ${selectedFiles.size} file(s)..."
                            else -> "Upload ${selectedFiles.size} file(s)"
                        }
                    )
                }
            }
        }
    }
}