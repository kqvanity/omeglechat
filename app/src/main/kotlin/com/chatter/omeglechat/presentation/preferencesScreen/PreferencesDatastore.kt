package com.chatter.omeglechat.presentation.preferencesScreen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chatter.omeglechat.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

class PreferencesRepository(
    private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "user_preferences",
        produceMigrations = {
            emptyList()
        },
        corruptionHandler = null,
//        scope = CoroutineScope(Dispatchers.IO)
    )

    // Create some keys
    companion object {
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey(name = "enable_notifications")
        val ENABLE_DARK_MODE = booleanPreferencesKey(name = "enable_dark_mode")
        val ENABLE_LANGUAGE_MATCH = booleanPreferencesKey(name = "enable_language_match")
        val AUTO_REPLY = booleanPreferencesKey(name = "auto_reply")
        val AUTO_SKIP = booleanPreferencesKey(name = "auto_skip")
        val USER_AGE = intPreferencesKey(name = "user_age")
        val USER_LANGUAGE = stringPreferencesKey(name = "user_language")
        val USER_INTERESTS = stringPreferencesKey(name = "user_interests")
    }

    private val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch {
            // Throws an IO exception when an error is encountered when reading data
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw (it)
            }
        }
        .map { prefs ->
            UserPreferences(
                enableNotifications = prefs[ENABLE_NOTIFICATIONS] ?: false,
                darkMode = prefs[ENABLE_DARK_MODE] ?: false,
                languageMatch = prefs[ENABLE_LANGUAGE_MATCH] ?: false,
                language = prefs[USER_LANGUAGE] ?: LANGUAGES.ENGLISH.code,
                userInterests = prefs[USER_INTERESTS] ?: "",
                autoReply = prefs[AUTO_REPLY] ?: false,
                autoSkip = prefs[AUTO_SKIP] ?: false,
                age = prefs[USER_AGE] ?: 18
            )
        }

    fun <T> getUserData(
        key: Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> = context.dataStore.data.map { prefs ->
        prefs[key] ?: defaultValue
    }

    suspend fun <T> rememberPreference(
        key: Preferences.Key<T>,
        defaultValue: T
    ) = context.dataStore.edit { prefs ->
        prefs[key] = defaultValue
    }

    suspend fun togglePref(key: Preferences.Key<Boolean>) = context.dataStore.edit { prefs ->
        prefs[key] = !(prefs[key] ?: false)
    }

}

enum class LANGUAGES(val code: String) {
    ENGLISH("EN")
}