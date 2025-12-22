package com.example.physicshub.ui.language

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

private val Context.translationDataStore by preferencesDataStore(name = "translations")

class TranslationRepository(private val context: Context) {

    private val gson = Gson()

    companion object {
        private const val TAG = "TranslationRepository"
        private val ENGLISH_TRANSLATIONS = stringPreferencesKey("english_translations")
        private val VIETNAMESE_TRANSLATIONS = stringPreferencesKey("vietnamese_translations")
        private val LAST_FETCH_TIME = stringPreferencesKey("last_fetch_time")
        private const val TRANSLATIONS_URL = "https://raw.githubusercontent.com/jacqueline-tlinh/PhysicsHub/main/translations.json"
        private const val CACHE_DURATION = 24 * 60 * 60 * 1000L

        @Volatile
        private var instance: TranslationRepository? = null

        fun getInstance(context: Context): TranslationRepository {
            return instance ?: synchronized(this) {
                instance ?: TranslationRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    val englishStrings: Flow<TranslationStrings> = context.translationDataStore.data.map { prefs ->
        val json = prefs[ENGLISH_TRANSLATIONS]
        if (json != null) {
            try {
                gson.fromJson(json, TranslationStrings::class.java)
            } catch (e: Exception) {
                defaultEnglishStrings
            }
        } else {
            defaultEnglishStrings
        }
    }

    val vietnameseStrings: Flow<TranslationStrings> = context.translationDataStore.data.map { prefs ->
        val json = prefs[VIETNAMESE_TRANSLATIONS]
        if (json != null) {
            try {
                gson.fromJson(json, TranslationStrings::class.java)
            } catch (e: Exception) {
                defaultVietnameseStrings
            }
        } else {
            defaultVietnameseStrings
        }
    }

    suspend fun getEnglishStrings(): TranslationStrings {
        return englishStrings.first()
    }

    suspend fun getVietnameseStrings(): TranslationStrings {
        return vietnameseStrings.first()
    }

    suspend fun fetchAndCacheTranslations(forceRefresh: Boolean = false): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh && !shouldRefreshCache()) {
                    Log.d(TAG, "Using cached translations")
                    return@withContext Result.success(Unit)
                }

                Log.d(TAG, "Fetching translations from server...")
                val response = fetchTranslationsFromServer()

                if (response != null) {
                    cacheTranslations(response)
                    Log.d(TAG, "Translations cached successfully")
                    Result.success(Unit)
                } else {
                    Log.w(TAG, "Failed to fetch translations, using defaults")
                    Result.failure(Exception("Failed to fetch translations"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching translations", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun shouldRefreshCache(): Boolean {
        val prefs = context.translationDataStore.data.first()
        val lastFetchTime = prefs[LAST_FETCH_TIME]?.toLongOrNull() ?: 0L
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastFetchTime) > CACHE_DURATION
    }

    private fun fetchTranslationsFromServer(): TranslationResponse? {
        return try {
            val url = URL(TRANSLATIONS_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                gson.fromJson(response, TranslationResponse::class.java)
            } else {
                Log.e(TAG, "API FAILED - Response Code: $responseCode")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "NETWORK ERROR: ${e.message}")
            null
        }
    }

    private suspend fun cacheTranslations(response: TranslationResponse) {
        context.translationDataStore.edit { prefs ->
            prefs[ENGLISH_TRANSLATIONS] = gson.toJson(response.en)
            prefs[VIETNAMESE_TRANSLATIONS] = gson.toJson(response.vn)
            prefs[LAST_FETCH_TIME] = System.currentTimeMillis().toString()
        }
    }

    suspend fun clearCache() {
        context.translationDataStore.edit { prefs: MutablePreferences ->
            prefs.clear()
        }
    }
}
