package com.example.physicshub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.physicshub.ui.language.Language
import com.example.physicshub.ui.language.LanguageManager
import com.example.physicshub.ui.language.LocalLanguage
import com.example.physicshub.ui.language.LocalTranslations
import com.example.physicshub.ui.language.TranslationRepository
import com.example.physicshub.ui.language.defaultEnglishStrings
import com.example.physicshub.ui.language.defaultVietnameseStrings
import com.example.physicshub.ui.navigation.PhysicsHubNavGraph
import com.example.physicshub.ui.theme.PhysicsHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val languageManager = remember { LanguageManager.getInstance(this) }
            val translationRepository = remember { TranslationRepository.getInstance(this) }

            val currentLanguage by languageManager.currentLanguage.collectAsState(initial = Language.ENGLISH)
            val englishStrings by translationRepository.englishStrings.collectAsState(initial = defaultEnglishStrings)
            val vietnameseStrings by translationRepository.vietnameseStrings.collectAsState(initial = defaultVietnameseStrings)

            LaunchedEffect(Unit) {
                translationRepository.fetchAndCacheTranslations()
            }

            CompositionLocalProvider(
                LocalLanguage provides currentLanguage,
                LocalTranslations provides Pair(englishStrings, vietnameseStrings)
            ) {
                PhysicsHubTheme {
                    PhysicsHubNavGraph()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemePreview() {
    PhysicsHubTheme {
        Text(
            text = "PhysicsHub Preview",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
