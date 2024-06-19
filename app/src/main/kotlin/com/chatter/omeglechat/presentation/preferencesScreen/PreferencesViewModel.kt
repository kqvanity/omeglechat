package com.chatter.omeglechat.presentation.preferencesScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow

class PreferencesViewModel(
    application: Application
): AndroidViewModel(application) {

    private val prefDataKeyValueStore = PreferencesRepository(
        context = application.applicationContext,
        scope = viewModelScope
    )

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

}