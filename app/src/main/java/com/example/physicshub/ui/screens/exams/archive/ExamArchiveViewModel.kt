package com.example.physicshub.ui.screens.exams.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physicshub.data.model.ExamMetadata
import com.example.physicshub.data.model.ExamPaper
import com.example.physicshub.data.repository.ExamArchiveRepository
import com.example.physicshub.data.repository.MetadataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExamArchiveViewModel(
    private val metadataRepository: MetadataRepository = MetadataRepository(),
    private val archiveRepository: ExamArchiveRepository = ExamArchiveRepository()
) : ViewModel() {

    private val _metadata = MutableStateFlow<List<ExamMetadata>>(emptyList())
    val metadata: StateFlow<List<ExamMetadata>> = _metadata.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _examPapers = MutableStateFlow<List<ExamPaper>>(emptyList())
    val examPapers: StateFlow<List<ExamPaper>> = _examPapers.asStateFlow()

    private val _loadingPapers = MutableStateFlow(false)
    val loadingPapers: StateFlow<Boolean> = _loadingPapers.asStateFlow()

    private val _currentExam = MutableStateFlow<ExamPaper?>(null)
    val currentExam: StateFlow<ExamPaper?> = _currentExam.asStateFlow()

    init {
        loadMetadata()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            _loading.value = true
            _metadata.value = metadataRepository.getExamMetadata()
            _loading.value = false
        }
    }

    fun loadExamsByCourse(division: String, category: String, course: String) {
        viewModelScope.launch {
            _loadingPapers.value = true
            _examPapers.value = archiveRepository.getExamsByCourse(division, category, course)
            _loadingPapers.value = false
        }
    }

    fun loadExamById(examId: String) {
        viewModelScope.launch {
            _loading.value = true
            _currentExam.value = archiveRepository.getExamById(examId)
            _loading.value = false
        }
    }

    fun getNewestUploads(limit: Int = 10, callback: (List<ExamPaper>) -> Unit) {
        viewModelScope.launch {
            val uploads = archiveRepository.getNewestUploads(limit)
            callback(uploads)
        }
    }
}