package com.chatter.omeglechat.preferences

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(application: Application): AndroidViewModel(application) {

    //    private val prefDataKeyValueStore = PreferencesDataStore(context = application.applicationContext)
    private val prefDataKeyValueStore = PreferencesDataStore(context = getApplication<Application>().applicationContext)
    fun saveUserInterests(commonInterests: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            // I guess this should be place elsewhere e.g., when the outlined text field loses focus
            prefDataKeyValueStore.updateUserInterests(
                commonInterests = commonInterests.joinToString(separator = ",")
            )
        }
    }

    init {
        viewModelScope.launch {
//            _commonInterests.clear()
//            _commonInterests.addAll(
                prefDataKeyValueStore.getUserInterests()
//                    .asLiveData()
//                    .value
//                    .toMutableStateList()
                    .stateIn(viewModelScope)
                    .value
                    .toMutableStateList()
//            )
        }
    }

}