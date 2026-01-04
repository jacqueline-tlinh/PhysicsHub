package com.example.physicshub.ui.screens.exams.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physicshub.data.model.ExamMetadata
import com.example.physicshub.data.model.ExamType
import com.example.physicshub.data.model.FileType
import com.example.physicshub.data.model.Semester
import com.example.physicshub.data.repository.ExamUploadRepository
import com.example.physicshub.data.repository.MetadataRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamUploadViewModel(
    private val metadataRepository: MetadataRepository = MetadataRepository(),
    private val uploadRepository: ExamUploadRepository = ExamUploadRepository()
) : ViewModel() {

    // ---------- METADATA ----------

    private val _metadata = MutableStateFlow<List<ExamMetadata>>(emptyList())
    val metadata: StateFlow<List<ExamMetadata>> = _metadata

    private val _loadingMetadata = MutableStateFlow(true)
    val loadingMetadata: StateFlow<Boolean> = _loadingMetadata

    init {
        loadMetadata()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            _loadingMetadata.value = true
            _metadata.value = metadataRepository.getExamMetadata()
            _loadingMetadata.value = false
        }
    }

    // ---------- SELECTION STATE ----------

    private val _division = MutableStateFlow<String?>(null)
    val division: StateFlow<String?> = _division

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category

    private val _course = MutableStateFlow<String?>(null)
    val course: StateFlow<String?> = _course

    private val _examType = MutableStateFlow<ExamType?>(null)
    val examType: StateFlow<ExamType?> = _examType

    private val _semester = MutableStateFlow<Semester?>(null)
    val semester: StateFlow<Semester?> = _semester

    private val _year = MutableStateFlow<Int?>(null)
    val year: StateFlow<Int?> = _year

    // ---------- DERIVED LISTS (FOR DROPDOWNS) ----------

    val divisions: StateFlow<List<String>> =
        metadata
            .map { list: List<ExamMetadata> ->
                list.map { it.division }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    val categories: StateFlow<List<String>> =
        combine(
            metadata,
            division
        ) { data: List<ExamMetadata>, selectedDivision: String? ->
            data
                .find { it.division == selectedDivision }
                ?.categories
                ?.map { it.name }
                ?: emptyList()
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    val courses: StateFlow<List<String>> =
        combine(
            metadata,
            division,
            category
        ) { data: List<ExamMetadata>, d: String?, c: String? ->
            data
                .find { it.division == d }
                ?.categories
                ?.find { it.name == c }
                ?.courses
                ?: emptyList()
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    // ---------- VALIDATION ----------

    val canUpload: StateFlow<Boolean> =
        combine(
            division,
            category,
            course,
            examType,
            semester,
            year
        ) { values ->
            values.all { it != null }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            false
        )

    // ---------- SELECTORS ----------

    fun selectDivision(value: String) {
        _division.value = value
        _category.value = null
        _course.value = null
    }

    fun selectCategory(value: String) {
        _category.value = value
        _course.value = null
    }

    fun selectCourse(value: String) {
        _course.value = value
    }

    fun selectExamType(value: ExamType) {
        _examType.value = value
    }

    fun selectSemester(value: Semester) {
        _semester.value = value
    }

    fun selectYear(value: Int) {
        _year.value = value
    }

    // ---------- UPLOAD STATE ----------

    sealed interface UploadState {
        object Idle : UploadState
        object Uploading : UploadState
        object Success : UploadState
        data class Error(val message: String) : UploadState
    }

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    // ---------- UPLOAD ----------

    fun uploadExam(
        fileUri: Uri,
        fileType: FileType,
        fileSize: Long,
        uploadedBy: String,
        role: String
    ) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading

            val result = uploadRepository.uploadExamPaper(
                fileUri = fileUri,
                fileType = fileType,
                fileSize = fileSize,

                division = division.value!!,
                category = category.value!!,
                course = course.value!!,
                examType = examType.value!!.name,
                semester = semester.value!!.name,
                year = year.value!!,

                uploadedBy = uploadedBy,
                role = role
            )

            _uploadState.value =
                result.fold(
                    onSuccess = { UploadState.Success },
                    onFailure = {
                        UploadState.Error(it.message ?: "Upload failed")
                    }
                )
        }
    }

    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }
}
