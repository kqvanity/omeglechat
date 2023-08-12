package com.chatter.omeglechat.ChatScreen

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import com.polendina.lib.NewConnection

import com.chatter.omeglechat.Message
import kotlinx.coroutines.flow.MutableStateFlow

/*
    -
        - Extension function that consider the application's lifecycle.
        -  Collect From Kotlin Flows While Considering the app Lifecycle In Android Jetpack Compose
            - If the app's lifecycle is ignored, it can lead to an unexpected behavior.
                - It can consume unnecessary resources.
*/

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var _messages = mutableStateListOf<Message>()
    private var counter = MutableStateFlow(value = listOf<String>())
    //    private var _scrollState = rememberScrollState()
    private var _connectionState by mutableStateOf(String())
    private var _newConnection by mutableStateOf(NewConnection())
    private var _commonInterests = mutableStateListOf<String>()

    var newConnection
        get() = _newConnection
        set(value) { _newConnection = value }
    var connectionState
        get() = _connectionState
        set(value) { _connectionState = value }

    var messages
        get() = _messages
        set(value) { _messages = value }

    var commonInterests
        get() = _commonInterests
        set(value) { _commonInterests = value }

}