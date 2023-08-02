package com.chatter.omeglechat.ChatScreen

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polendina.lib.NewConnection

import com.chatter.omeglechat.Message
import com.chatter.omeglechat.preferences.PreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

//    private val prefDataKeyValueStore = PreferencesDataStore(context = application.applicationContext)
    private val prefDataKeyValueStore = PreferencesDataStore(context = getApplication<Application>().applicationContext)

    private var _messages = mutableStateListOf<Message>()

    //    private var _scrollState = rememberScrollState()
    private var _connectionState = mutableStateOf<String>(String())
    private var _newConnection = mutableStateOf<NewConnection>(NewConnection())
    private var _commonInterests = mutableStateListOf<String>()

//    val scrollState = _scrollState

    fun getNewConnection(): MutableState<NewConnection> {
        return (_newConnection)
    }

    fun getConnectionState(): MutableState<String> {
        println("Getting connection state ${_connectionState}")
        return (_connectionState)
    }

    fun updateConnectionState(newState: String) {
        println("Updating connection state ${_connectionState}")
        _connectionState.value = newState
    }

    fun getMessages(): SnapshotStateList<Message> {
        return (_messages)
    }

    fun addMessage(message: Message) {
        _messages.add(message)
    }

    fun clearMessages() {
        _messages.clear()
    }

    fun getCommonInterests(): List<String> {
        return (_commonInterests)
    }

    fun updateCommonInterests(commonInterests: List<String>) {
        _commonInterests.clear()
        _commonInterests.addAll(commonInterests)
        CoroutineScope(IO).launch {
            // I guess this should be place elsewhere e.g., when the outlined text field loses focus
            prefDataKeyValueStore.updateCommonInterests(
                commonInterests = commonInterests.joinToString(separator = ",")
            )
        }
    }

//    fun getScrollState(): LazyListState = {
//        return (_scrollState)
//    }

    init {
        viewModelScope.launch {
            _commonInterests.clear()
            _commonInterests.addAll(
                prefDataKeyValueStore.getUserInterests()
                    .stateIn(viewModelScope)
                    .value
                    .toMutableStateList()
            )
        }
//        updateCommonInterests(commonInterests = PreferencesDataStore(context = context).getUserInterests().collectAsState(
//            initial = emptyList()
//        ).value)
        // Following code causes IllegalStateException
//        CoroutineScope(context = Dispatchers.Default).launch {
//            PreferencesDataStore(context = context).getUserInterests().collect {
//                updateCommonInterests(commonInterests = it)
//            }
//        }
    }

}