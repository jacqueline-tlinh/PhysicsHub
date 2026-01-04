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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.physicshub.util.FileValidation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamUploadScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ExamUploadViewModel = viewModel()

    var selectedFile by remember { mutableStateOf<Uri?>(null) }

    val metadata by viewModel.metadata.collectAsState()
    val loadingMetadata by viewModel.loadingMetadata.collectAsState()

    LaunchedEffect(metadata) {
        println("EXAM METADATA FROM FIREBASE:")
        metadata.forEach {
            println(it)
        }
    }

    val uploadState by viewModel.uploadState.collectAsState()
    val canUpload by viewModel.canUpload.collectAsState()

    val resolver = context.contentResolver
    val mimeType = selectedFile?.let { resolver.getType(it) }
    val fileType = mimeType?.let { FileValidation.getFileType(it) }

    val subject by viewModel.course.collectAsState()

    val course by viewModel.course.collectAsState()

    var subjectExpanded by remember { mutableStateOf(false) }
    var courseExpanded by remember { mutableStateOf(false) }

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
                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = !subjectExpanded }
                ) {
                    OutlinedTextField(
                        value = subject ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Subject") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                }

                val selectedMetadata = metadata.find { it.title == subject }
                val divisions = selectedMetadata?.divisions.orEmpty()

                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    OutlinedTextField(
                        value = subject ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Course") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
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
                    Text("Select PDF or Image")
                }

                Button(
                    enabled = selectedFile != null &&
                            canUpload &&
                            uploadState !is ExamUploadViewModel.UploadState.Uploading,
                    onClick = {
                        viewModel.uploadExam(
                            fileUri = selectedFile!!,
                            fileType = fileType!!,
                            fileSize = fileSize,
                            uploadedBy = "demoUser",   // replace later
                            role = "student"           // or "admin"
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
