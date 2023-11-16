package com.chatter.omeglechat.presentation.preferencesScreen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

private const val USER_PREFERENCES_NAME = "user_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    produceMigrations = {
        emptyList()
    },
    corruptionHandler = null,
    scope = CoroutineScope(Dispatchers.IO)
)