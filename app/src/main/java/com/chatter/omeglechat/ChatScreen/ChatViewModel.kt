package com.chatter.omeglechat.ChatScreen

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polendina.coreLib.NewConnection
import kotlinx.coroutines.CoroutineScope

import com.chatter.omeglechat.Message

class ChatViewModel: ViewModel() {

    private val _messages = MutableLiveData<SnapshotStateList<Message>>()
    private val _darkThemeState = MutableLiveData<Boolean>()
    private val _coroutineScope = MutableLiveData<CoroutineScope>()
    private val _scrollState = MutableLiveData<LazyListState>()
    private val _commonInterests = MutableLiveData<MutableList<String>>(mutableStateListOf())
    private val _connectionState = MutableLiveData<String>(String())
    private val _newConnection = MutableLiveData<NewConnection>()

    //    val messages = remember { mutableStateListOf<Message>(*messagess.toTypedArray()) }  // Converting a list to a vararg argument. nap
    val messages = _messages
    // The current implementation is just a placeholder before actually implementing it properly with a toggle button and whatnot
    val darkThemeState = _darkThemeState
    val coroutineScope = _coroutineScope
    val scrollState = _scrollState
    val commonInterests = _commonInterests
    val connectionState = _connectionState
    val newConnection = _newConnection

    fun updateConnectionState(newState: String) {
        _connectionState.postValue(newState)
    }

    fun clearMessages() {
        _messages.postValue(mutableStateListOf())
    }
    fun clearCommonInterests() {
        _commonInterests.postValue(mutableStateListOf())
    }
    fun addAllCommonInterests(commonInterests: MutableList<String>) {
        _commonInterests.postValue(commonInterests)
    }

}
val chatViewModel: ChatViewModel = ChatViewModel()
