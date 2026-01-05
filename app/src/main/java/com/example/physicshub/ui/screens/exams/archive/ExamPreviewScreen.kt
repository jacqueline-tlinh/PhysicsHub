package com.example.physicshub.ui.screens.exams.archive

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.physicshub.data.model.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPreviewScreen(
    navController: NavController,
    examId: String,
    viewModel: ExamArchiveViewModel = viewModel()
) {
    val currentExam by viewModel.currentExam.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(examId) {
        viewModel.loadExamById(examId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Preview", style = MaterialTheme.typography.headlineMedium)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Download") },
                            onClick = {
                                menuExpanded = false
                                currentExam?.let {
                                    openUrl(context, it.fileUrl)
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (loading || currentExam == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val exam = currentExam!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Course name
                Text(
                    text = exam.course,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetadataItem("Division", exam.division)
                    MetadataItem("Category", exam.category)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetadataItem("Type", exam.examType)
                    MetadataItem("Year", exam.year.toString())
                }

                // File info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoRow("File Type", exam.fileType.name)
                        InfoRow("File Size", "${exam.fileSize / 1024} KB")
                    }
                }

                // ===== PREVIEW =====
                when (exam.fileType) {
                    FileType.IMAGE -> {
                        ImagePreview(
                            url = exam.fileUrl,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }

                    FileType.PDF -> {
                        var pdfFile by remember { mutableStateOf<File?>(null) }

                        LaunchedEffect(exam.fileUrl) {
                            pdfFile = downloadPdfToCache(
                                context = context,
                                url = exam.fileUrl,
                                fileName = exam.id
                            )
                        }

                        if (pdfFile == null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            PdfPreview(
                                pdfFile = pdfFile!!,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(500.dp)
                            )
                        }
                    }
                }

                // Download button
                Button(
                    onClick = { openUrl(context, exam.fileUrl) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Download")
                }
            }
        }
    }
}

/* ---------- PREVIEW HELPERS ---------- */

@Composable
fun ImagePreview(url: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url,
        contentDescription = "Exam image",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun PdfPreview(pdfFile: File, modifier: Modifier = Modifier) {
    val renderer = remember(pdfFile) {
        PdfRenderer(
            ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        )
    }

    LazyColumn(modifier = modifier) {
        items(renderer.pageCount) { index ->
            val page = renderer.openPage(index)

            val bitmap = Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "PDF page ${index + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

/* ---------- UTILS ---------- */

suspend fun downloadPdfToCache(
    context: Context,
    url: String,
    fileName: String
): File = withContext(Dispatchers.IO) {
    val file = File(context.cacheDir, "$fileName.pdf")
    if (file.exists()) return@withContext file

    val request = Request.Builder().url(url).build()
    val client = OkHttpClient()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) error("Failed to download PDF")
        file.outputStream().use { output ->
            response.body!!.byteStream().copyTo(output)
        }
    }

    file
}

fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

/* ---------- SMALL UI PARTS ---------- */

@Composable
private fun MetadataItem(label: String, value: String) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
