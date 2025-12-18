package com.example.physicshub.ui.screens.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class LoginViewModel : ViewModel() {

    private val _uiState = mutableStateOf(LoginUiState())
    val uiState: State<LoginUiState> = _uiState

    fun onStudentIdChange(value: String) {
        _uiState.value = _uiState.value.copy(studentId = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun login() {
        // For now: simulate success if both fields are non-empty
        if (_uiState.value.studentId.isNotBlank() &&
            _uiState.value.password.isNotBlank()
        ) {
            _uiState.value = _uiState.value.copy(
                isLoginSuccess = true,
                errorMessage = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter both ID and password"
            )
        }
    }

    fun consumeLoginSuccess() {
        // Prevent repeated navigation on recomposition
        _uiState.value = _uiState.value.copy(isLoginSuccess = false)
    }
}
