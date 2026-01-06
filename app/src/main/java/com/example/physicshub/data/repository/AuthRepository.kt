package com.example.physicshub.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.physicshub.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    private object PreferencesKeys {
        val STUDENT_ID = stringPreferencesKey("student_id")
        val FULL_NAME = stringPreferencesKey("full_name")
        val ROLE = stringPreferencesKey("role")
    }

    /**
     * Login with student ID and password
     */
    suspend fun login(studentId: String, password: String): Result<User> {
        return try {
            val snapshot = firestore
                .collection("users")
                .document(studentId)
                .get()
                .await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Student ID not found"))
            }

            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("Invalid user data"))

            // Check password
            if (user.password != password) {
                return Result.failure(Exception("Incorrect password"))
            }

            // Save user session
            saveUserSession(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Save user session to DataStore
     */
    private suspend fun saveUserSession(user: User) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.STUDENT_ID] = user.studentId
            prefs[PreferencesKeys.FULL_NAME] = user.fullName
            prefs[PreferencesKeys.ROLE] = user.role
        }
    }

    /**
     * Get current logged-in user
     */
    fun getCurrentUser(): Flow<User?> {
        return context.dataStore.data.map { prefs ->
            val studentId = prefs[PreferencesKeys.STUDENT_ID]
            val fullName = prefs[PreferencesKeys.FULL_NAME]
            val role = prefs[PreferencesKeys.ROLE]

            if (studentId != null && fullName != null) {
                User(
                    studentId = studentId,
                    fullName = fullName,
                    role = role ?: "student"
                )
            } else {
                null
            }
        }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[PreferencesKeys.STUDENT_ID] != null
        }
    }

    /**
     * Logout
     */
    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}