package com.chatter.omeglechat.presentation.preferencesScreen

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(application: Application): AndroidViewModel(application) {

    private val prefDataKeyValueStore = PreferencesRepository(context = application.applicationContext)

    private var _userInterests = mutableStateListOf<String>()
    var userInterests = _userInterests
        set(value) {
            field.clear()
            field.addAll(value)
            viewModelScope.launch {
                prefDataKeyValueStore.rememberPreference(
                    key = PreferencesRepository.USER_INTERESTS,
                    defaultValue = value.joinToString(separator = ",")
                )
            }
        }

    init {
         viewModelScope.launch {
             _userInterests.addAll(
                 prefDataKeyValueStore
                     .getUserData(key = PreferencesRepository.USER_INTERESTS)
                     .stateIn(viewModelScope)
                     .value?.split(",") ?: emptyList()
             )
        }
    }

}