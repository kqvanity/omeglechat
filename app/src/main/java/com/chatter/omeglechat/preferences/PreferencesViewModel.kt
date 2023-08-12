package com.chatter.omeglechat.preferences

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(application: Application): AndroidViewModel(application) {

    //    private val prefDataKeyValueStore = PreferencesDataStore(context = application.applicationContext)
    private val prefDataKeyValueStore = PreferencesDataStore(context = getApplication<Application>().applicationContext)

    // todo: I still have no idea how am I gonna return a value from within the coroutine scope i.e., viewModelScope
//    private var _userInterests: List<String> = listOf()
//    var userInterests
//        get() {
//            viewModelScope.launch {
//                prefDataKeyValueStore.getUserInterests()
//            }
//        }
//        set(value) {
//            viewModelScope.launch {
//                // I guess this should be place elsewhere e.g., when the outlined text field loses focus
//                prefDataKeyValueStore.updateUserInterests(
//                     userInterests = _userInterests.joinToString(separator = ",")
//                )
//            }
//        }

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