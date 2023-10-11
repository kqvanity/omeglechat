package com.chatter.omeglechat.ChatScreen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatter.omeglechat.data.network.Connection
import com.chatter.omeglechat.domain.model.ConnectionStates
import com.chatter.omeglechat.preferences.PreferencesRepository
import com.chatter.omeglechat.presentation.ChatScreen.chatMessages
import com.chatter.omeglechat.presentation.ChatScreen.commonInterest
import com.chatter.omeglechat.presentation.preferencesScreen.dataStore
import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface ChatViewModel {
    var messages: SnapshotStateList<Message>
    var connectionState: String
    var commonInterests: SnapshotStateList<String>
    var textMessage: MutableState<String>
    fun sendTextMessage()
    fun terminate()
}

class ChatViewModelImpl(
    application: Application = Application(),
) : ChatViewModel, AndroidViewModel(application) {
    private val connection by lazy { Connection() }
    override var messages = mutableStateListOf<Message>()

    override var connectionState by mutableStateOf(String())
    override var commonInterests = mutableStateListOf<String>()

    // General user interests (saved in the settings DataStore, and the matching interests returned by Omegle's server)
    private lateinit var userInterests: List<String>

    private val prohibitedIds = mutableStateListOf<String>()

    override var textMessage = mutableStateOf("")

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun sendTextMessage() {
        ioScope.launch {
            connection.sendText(textMessage.value)
            messages.add(Message(0, textMessage.value))
            connectionState = ConnectionStates.MESSAGE.displayName
            textMessage.value = ""
        }
    }

    override fun terminate() {
        messages.clear()
        connectionState = ConnectionStates.DISCONNECTED.displayName
        commonInterests.clear()
        ioScope.launch {
            connection.disconnect()
            connection.commonInterests = userInterests
            connection.start()
        }
    }

    private fun initializeObservers() {
        connection.connectionObserver = object : ConnectionObserver {
            override fun onConnected(usersCommonInterests: List<String>) {
                connectionState = ConnectionStates.CONNECTED.displayName
                commonInterests = usersCommonInterests.toMutableStateList()
                if (prohibitedIds.contains(connection.clientId)) {
                    terminate()
                }
            }
            override fun onRecaptchaRequired() { connectionState = ConnectionStates.RECAPTCHA_REQUIRED.displayName }
            override fun onTyping() { connectionState = ConnectionStates.TYPING.displayName }
            override fun onStoppedTyping() { connectionState = ConnectionStates.STALE.displayName }
            override fun onUserDisconnected() {
                connectionState = ConnectionStates.DISCONNECTED.displayName
                commonInterests.clear()
                connection.commonInterests = userInterests.toMutableList()
                connection.clientId = ""
            }
            override fun onConnectionError() { TODO("Not yet implemented") }
            override fun onWaiting() { connectionState = ConnectionStates.WAITING.displayName }
            override fun onGotMessage(message: String) {
                connectionState = ConnectionStates.MESSAGE.displayName
                messages.add(Message(1, message))
            }
            override fun onError() { Log.d("ERROR", "Error Occurred") }
            override fun onEvent(response: String) { Log.d("ERROR", "Event!") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // TODO: I'm not sure if it's technically working or not!
        ioScope.launch {
            connection.disconnect()
        }
    }

    init {
        viewModelScope.launch {
            val preferencesRepository = PreferencesRepository(application.applicationContext.dataStore)
            userInterests = preferencesRepository.getUserInterests().first()
            connection.commonInterests = userInterests
            initializeObservers()
        }
    }

}

data class Message(
    val id: Int,
    val text: String
)

class ChatViewModelMock(): ChatViewModel {
    override var commonInterests: SnapshotStateList<String>
        get() = commonInterest.toMutableStateList()
        set(value) {}
    override var connectionState: String
        get() = ConnectionStates.entries.random().state
        set(value) {}
    override var messages: SnapshotStateList<Message>
        get() = chatMessages.toMutableStateList()
        set(value) {}
    override var textMessage: MutableState<String>
        get() = mutableStateOf(chatMessages.random().text)
        set(value) {}

    override fun sendTextMessage() { }
    override fun terminate() { }
}