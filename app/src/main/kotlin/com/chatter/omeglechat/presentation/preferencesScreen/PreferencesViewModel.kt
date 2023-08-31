package com.chatter.omeglechat.preferences

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(application: Application): AndroidViewModel(application) {

//    private val prefDataKeyValueStore = PreferencesDataStore(context = application.applicationContext)
    private val prefDataKeyValueStore = PreferencesDataStore(context = getApplication<Application>().applicationContext)

    private var _userInterests = mutableStateListOf<String>()
    var userInterests = _userInterests
        set(value) {
            field.clear()
            field.addAll(value)
            viewModelScope.launch {
                prefDataKeyValueStore.updateUserInterests(
                    userInterests = value.joinToString(separator = ",")
                )
            }
        }

    init {
         viewModelScope.launch {
             _userInterests.addAll(prefDataKeyValueStore.getUserInterests().stateIn(viewModelScope).value.toMutableStateList())
        }
    }

}