package com.example.physicshub.ui.screens.exams.upload

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.physicshub.data.model.ExamType
import com.example.physicshub.data.model.Semester
import com.example.physicshub.util.FileValidation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamUploadScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ExamUploadViewModel = viewModel()

    var selectedFile by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val metadata by viewModel.metadata.collectAsState()
    val loadingMetadata by viewModel.loadingMetadata.collectAsState()

    val uploadState by viewModel.uploadState.collectAsState()
    val canUpload by viewModel.canUpload.collectAsState()

    val divisions by viewModel.divisions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val courses by viewModel.courses.collectAsState()

    val selectedDivision by viewModel.division.collectAsState()
    val selectedCategory by viewModel.category.collectAsState()
    val selectedCourse by viewModel.course.collectAsState()
    val selectedExamType by viewModel.examType.collectAsState()
    val selectedSemester by viewModel.semester.collectAsState()
    val selectedYear by viewModel.year.collectAsState()

    var divisionExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var courseExpanded by remember { mutableStateOf(false) }
    var examTypeExpanded by remember { mutableStateOf(false) }
    var semesterExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val resolver = context.contentResolver
    val mimeType = selectedFile?.let { resolver.getType(it) }
    val fileType = mimeType?.let { FileValidation.getFileType(it) }

    val fileSize = selectedFile?.let {
        resolver.openFileDescriptor(it, "r")?.statSize ?: 0L
    } ?: 0L

    val filePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                if (FileValidation.isValidFile(context, uri)) {
                    selectedFile = uri
                    // Get filename from URI
                    val cursor = resolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (displayNameIndex != -1) {
                                selectedFileName = it.getString(displayNameIndex)
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

    // ---------- UPLOAD RESULT HANDLING ----------
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is ExamUploadViewModel.UploadState.Success -> {
                Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
                viewModel.resetUploadState()
                navController.popBackStack()
            }

            is ExamUploadViewModel.UploadState.Error -> {
                Toast.makeText(
                    context,
                    (uploadState as ExamUploadViewModel.UploadState.Error).message,
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
                        value = selectedCourse ?: "",
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

                // Semester Dropdown
                ExposedDropdownMenuBox(
                    expanded = semesterExpanded,
                    onExpandedChange = { semesterExpanded = !semesterExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSemester?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Semester") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = semesterExpanded,
                        onDismissRequest = { semesterExpanded = false }
                    ) {
                        Semester.values().forEach { semester ->
                            DropdownMenuItem(
                                text = { Text(semester.name) },
                                onClick = {
                                    viewModel.selectSemester(semester)
                                    semesterExpanded = false
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

                // File Selection Button
                OutlinedButton(
                    onClick = {
                        filePickerLauncher.launch(
                            arrayOf(
                                "application/pdf",
                                "image/jpeg",
                                "image/png"
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (selectedFileName != null) {
                            "Selected: $selectedFileName"
                        } else {
                            "Select PDF or Image"
                        }
                    )
                }

                // Upload Button
                Button(
                    enabled = selectedFile != null && canUpload && uploadState !is ExamUploadViewModel.UploadState.Uploading,
                    onClick = {
                        viewModel.uploadExam(
                            fileUri = selectedFile!!,
                            fileType = fileType!!,
                            fileSize = fileSize,
                            uploadedBy = "demoUser",
                            role = "student"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when (uploadState) {
                            ExamUploadViewModel.UploadState.Uploading -> "Uploading..."
                            else -> "Upload"
                        }
                    )
                }
            }
        }
    }
}