package com.chatter.omeglechat.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.chatter.omeglechat.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

class PreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    // todo: other types of data structures aren't supported as of now for some preferences e.g., language, common interests, just because of my current lack of knowledge
    companion object {
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey(name = "enable_notifications")
        val ENABLE_DARK_MODE = booleanPreferencesKey(name = "enable_dark_mode")
        val ENABLE_LANGUAGE_MATCH = booleanPreferencesKey(name = "enable_language_match")
        val USER_LANGUAGE = stringPreferencesKey(name = "user_language")
        val USER_INTERESTS = stringPreferencesKey(name = "user_interests")
        val AUTO_REPLY = booleanPreferencesKey(name = "auto_reply")
        val AUTO_SKIP = booleanPreferencesKey(name = "auto_skip")
        val AGE = intPreferencesKey(name = "user_age")
    }

    private val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch {
            // Throws an IO exception when an error is encountered when reading data.
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw (it)
            }
        }
        .map {
            UserPreferences(
                enableNotifications = it[ENABLE_NOTIFICATIONS] ?: false,
                darkMode = it[ENABLE_DARK_MODE] ?: false,
                languageMatch = it[ENABLE_LANGUAGE_MATCH] ?: false,
                language = it[USER_LANGUAGE] ?: LANGUAGES.ENGLISH.code,
                userInterests = it[USER_INTERESTS] ?: "",
                autoReply = it[AUTO_REPLY] ?: false,
                autoSkip = it[AUTO_SKIP] ?: false,
                age = it[AGE] ?: 18
            )
        }

    fun getUserInterests(): Flow<List<String>> = dataStore.data.map {
        it[USER_INTERESTS]?.split(",") ?: emptyList()
    }
    suspend fun updateUserInterests(userInterests: String) = dataStore.edit {
        it[USER_INTERESTS] = userInterests
    }

}
enum class LANGUAGES(val code: String) {
    ENGLISH("EN")
}