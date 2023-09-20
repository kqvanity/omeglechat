package com.chatter.omeglechat.ChatScreen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import com.chatter.omeglechat.data.network.NewConnection
import com.chatter.omeglechat.domain.model.ConnectionStates
import com.chatter.omeglechat.preferences.PreferencesViewModel
import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
//) : ViewModel() {
    application: Application = Application()
) : AndroidViewModel(application) {

//    var messages = chatMessages.toMutableStateList()
    var messages = mutableStateListOf<Message>()
        private set
    private var counter = MutableStateFlow(value = listOf<String>())
    //    private var _scrollState = rememberScrollState()
    var connectionState by mutableStateOf(String())
        private set
//    private val newConnection by mutableStateOf(NewConnection())
    var commonInterests = mutableStateListOf<String>()
        private set

    // General user interests (saved in the settings DataStore, and the matching interests returned by Omegle's server)
    private var userInterests: MutableList<String>

    private val prohibitedIds = mutableStateListOf<String>()

    var textMessage = mutableStateOf("")

    val ioScope = CoroutineScope(Dispatchers.IO)

    fun sendTextMessage() {
        ioScope.launch {
            NewConnection.sendText(textMessage.value)
            messages.add(Message(0, textMessage.value))
            connectionState = ConnectionStates.MESSAGE.displayName
            textMessage.value = ""
        }
    }

    fun terminate() {
        messages.clear()
        connectionState = ConnectionStates.DISCONNECTED.displayName
        commonInterests.clear()
        ioScope.launch {
            NewConnection.disconnect()
            NewConnection.setCommonInterests(userInterests)
            NewConnection.start()
        }
    }

    private fun initializeObservers() {
        NewConnection.connectionObserver = object : ConnectionObserver {
            override fun onConnected(usersCommonInterests: List<String>) {
                connectionState = ConnectionStates.CONNECTED.displayName
                commonInterests = usersCommonInterests.toMutableStateList()
                if (prohibitedIds.contains(NewConnection.clientId)) {
                    terminate()
                }
            }
            override fun onRecaptchaRequired() { connectionState = ConnectionStates.RECAPTCHA_REQUIRED.displayName }
            override fun onTyping() { connectionState = ConnectionStates.TYPING.displayName }
            override fun onStoppedTyping() { connectionState = ConnectionStates.STALE.displayName }
            override fun onUserDisconnected() {
                connectionState = ConnectionStates.DISCONNECTED.displayName
                commonInterests.clear()
                NewConnection.setCommonInterests(userInterests.toMutableList())
                NewConnection.clientId = ""
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
            NewConnection.disconnect()
        }
    }

    init {
        userInterests = PreferencesViewModel(application).userInterests
        NewConnection.setCommonInterests(userInterests)
        initializeObservers()
        // Instantiating new connection when the the chat screen is loaded.
        ioScope.launch {
            NewConnection.start()
        }
    }
}

data class Message(
    val id: Int,
    val text: String
)