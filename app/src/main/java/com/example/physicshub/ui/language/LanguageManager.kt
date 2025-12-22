package com.example.physicshub.ui.language

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.languageDataStore by preferencesDataStore(name = "language_settings")

enum class Language(val code: String, val displayName: String) {
    ENGLISH("EN", "EN"),
    VIETNAMESE("VN", "VN")
}

val LocalLanguage = compositionLocalOf { Language.ENGLISH }

class LanguageManager(private val context: Context) {

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("selected_language")

        @Volatile
        private var instance: LanguageManager? = null

        fun getInstance(context: Context): LanguageManager {
            return instance ?: synchronized(this) {
                instance ?: LanguageManager(context.applicationContext).also { instance = it }
            }
        }
    }

    val currentLanguage: Flow<Language> = context.languageDataStore.data.map { preferences ->
        val code = preferences[LANGUAGE_KEY] ?: Language.ENGLISH.code
        Language.entries.find { it.code == code } ?: Language.ENGLISH
    }

    suspend fun setLanguage(language: Language) {
        context.languageDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
        }
    }

    suspend fun toggleLanguage() {
        context.languageDataStore.edit { preferences ->
            val currentCode = preferences[LANGUAGE_KEY] ?: Language.ENGLISH.code
            val newLanguage = if (currentCode == Language.ENGLISH.code) {
                Language.VIETNAMESE
            } else {
                Language.ENGLISH
            }
            preferences[LANGUAGE_KEY] = newLanguage.code
        }
    }
}
