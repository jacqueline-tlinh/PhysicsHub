package com.example.physicshub.ui.screens.login

data class LoginUiState(
    val studentId: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null
)