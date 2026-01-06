package com.example.physicshub.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý trạng thái Dark Mode
 * Theo kiến trúc MVVM
 */
class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themeManager = ThemeManager.getInstance(application)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        // Khôi phục theme đã lưu khi khởi động
        loadSavedTheme()
    }

    /**
     * Tải theme đã lưu từ DataStore
     */
    private fun loadSavedTheme() {
        viewModelScope.launch {
            themeManager.isDarkMode.collect { darkMode ->
                _isDarkMode.value = darkMode
            }
        }
    }

    /**
     * Chuyển đổi giữa Light và Dark Mode
     */
    fun toggleTheme() {
        viewModelScope.launch {
            themeManager.toggleTheme()
        }
    }

    /**
     * Đặt theme cụ thể
     * @param darkMode true = Dark Mode, false = Light Mode
     */
    fun setTheme(darkMode: Boolean) {
        viewModelScope.launch {
            themeManager.setDarkMode(darkMode)
        }
    }
}