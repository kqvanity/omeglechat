package com.chatter.omeglechat.presentation.preferencesScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatter.omeglechat.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PreferencesViewModel(
    application: Application
): AndroidViewModel(application) {

    private val prefDataKeyValueStore = PreferencesRepository(
        context = application.applicationContext,
        scope = viewModelScope
    )

    val settings: Flow<UserPreferences> = prefDataKeyValueStore.userPreferences
    var userInterests = prefDataKeyValueStore.getPrefsStateFlow(
        key = PreferencesRepository.USER_INTERESTS,
        initialValue = "",
        debounceLen = 100
    )
    var prohibitedWords = prefDataKeyValueStore.getPrefsStateFlow(
        key = PreferencesRepository.PROHIBITED_WORDS,
        initialValue = "",
        debounceLen = 100
    )

    init {
        viewModelScope.launch {
            with(settings.first()) {
                this@PreferencesViewModel.userInterests.value = userInterests
                this@PreferencesViewModel.prohibitedWords.value = prohibitedWords
            }
        }
    }
}