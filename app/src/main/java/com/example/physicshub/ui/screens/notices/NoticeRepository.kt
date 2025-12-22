package com.example.physicshub.ui.screens.notices

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notices")

class NoticeRepository(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val READ_NOTICE_IDS = stringSetPreferencesKey("read_notice_ids")
        private val CACHED_NOTICES = stringPreferencesKey("cached_notices")

        @Volatile
        private var instance: NoticeRepository? = null

        fun getInstance(context: Context): NoticeRepository {
            return instance ?: synchronized(this) {
                instance ?: NoticeRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    val readNoticeIds: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[READ_NOTICE_IDS] ?: emptySet()
    }

    val cachedNotices: Flow<List<Notice>> = context.dataStore.data.map { preferences ->
        val json = preferences[CACHED_NOTICES]
        if (json != null) {
            try {
                val type = object : TypeToken<List<NoticeDto>>() {}.type
                val dtos: List<NoticeDto> = gson.fromJson(json, type)
                dtos.map { it.toNotice() }
            } catch (e: Exception) {
                mockNotices
            }
        } else {
            mockNotices
        }
    }

    suspend fun markAsRead(noticeId: String) {
        context.dataStore.edit { preferences ->
            val currentIds = preferences[READ_NOTICE_IDS] ?: emptySet()
            preferences[READ_NOTICE_IDS] = currentIds + noticeId
        }
    }

    suspend fun markAsUnread(noticeId: String) {
        context.dataStore.edit { preferences ->
            val currentIds = preferences[READ_NOTICE_IDS] ?: emptySet()
            preferences[READ_NOTICE_IDS] = currentIds - noticeId
        }
    }

    suspend fun saveNotices(notices: List<Notice>) {
        context.dataStore.edit { preferences ->
            val dtos = notices.map { NoticeDto.fromNotice(it) }
            preferences[CACHED_NOTICES] = gson.toJson(dtos)
        }
    }

    suspend fun clearReadStates() {
        context.dataStore.edit { preferences ->
            preferences[READ_NOTICE_IDS] = emptySet()
        }
    }
}

private data class NoticeDto(
    val id: String,
    val category: String,
    val title: String,
    val content: String,
    val date: String
) {
    fun toNotice(): Notice = Notice(
        id = id,
        category = NoticeCategory.valueOf(category),
        title = title,
        content = content,
        date = date,
        isRead = false
    )

    companion object {
        fun fromNotice(notice: Notice): NoticeDto = NoticeDto(
            id = notice.id,
            category = notice.category.name,
            title = notice.title,
            content = notice.content,
            date = notice.date
        )
    }
}
