package com.chatter.omeglechat.ChatScreen

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
    private var _connectionState = mutableStateOf(String())
    private var _newConnection = mutableStateOf(NewConnection())
    private val _commonInterests = mutableStateListOf<String>()

//    val scrollState = _scrollState

    fun getNewConnection(): MutableState<NewConnection> {
        return (_newConnection)
    }

    fun getConnectionState(): MutableState<String> {
        return (_connectionState)
    }

    fun updateConnectionState(newState: String) {
        _connectionState.value = newState
    }

    // todo: I'm not sure if I should be using a full-fledged getter function. Also look up encapsulation.
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
    }

//    fun getScrollState(): LazyListState = {
//        return (_scrollState)
//    }

}