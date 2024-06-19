package com.chatter.omeglechat.presentation.preferencesScreen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chatter.omeglechat.domain.model.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings",
    produceMigrations = {
        emptyList()
    },
    corruptionHandler = null
)

class PreferencesRepository(
    private val context: Context,
    private val scope: CoroutineScope
) {

    companion object {
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey(name = "enable_notifications")
        val ENABLE_DARK_MODE = booleanPreferencesKey(name = "enable_dark_mode")
        val ENABLE_LANGUAGE_MATCH = booleanPreferencesKey(name = "enable_language_match")
        val AUTO_REPLY = booleanPreferencesKey(name = "auto_reply")
        val AUTO_SKIP = booleanPreferencesKey(name = "auto_skip")
        val USER_AGE = intPreferencesKey(name = "user_age")
        val USER_LANGUAGE = stringPreferencesKey(name = "user_language")
        val USER_INTERESTS = stringPreferencesKey(name = "user_interests")
        val PROHIBITED_WORDS = stringPreferencesKey(name = "prohibited_words")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data
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
                age = prefs[USER_AGE] ?: 18,
                prohibitedWords = prefs[PROHIBITED_WORDS] ?: ""
            )
        }
        .distinctUntilChanged()

    fun <T> getPrefs(
        key: Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> = context.dataStore.data.map { prefs ->
        prefs[key] ?: defaultValue
    }

    private suspend fun <T> rememberPrefs(
        key: Preferences.Key<T>,
        value: T
    ) = context.dataStore.edit { prefs ->
        prefs[key] = value
    }

    fun <T> getPrefsStateFlow(
        key: Preferences.Key<T>,
        initialValue: T,
        debounceLen: Long = 0
    ): MutableStateFlow<T> {
        val state = MutableStateFlow(initialValue)
        scope.launch {
            state.value = getPrefs(key = key, defaultValue = initialValue).first()
            state.debounce(debounceLen)
                .collectLatest { rememberPrefs(key = key, value = it) }
        }
        return state
    }

    suspend fun togglePref(key: Preferences.Key<Boolean>) = context.dataStore.edit { prefs ->
        prefs[key] = !(prefs[key] ?: false)
    }

    suspend fun <T> removePref(
        key: Preferences.Key<T>
    ) = context.dataStore.edit { prefs ->
        prefs.remove(key)
    }

}

enum class LANGUAGES(val code: String) {
    ENGLISH("EN")
}