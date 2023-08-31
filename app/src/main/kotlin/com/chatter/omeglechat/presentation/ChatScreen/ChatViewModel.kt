package com.chatter.omeglechat.ChatScreen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import com.polendina.lib.NewConnection

import com.chatter.omeglechat.preferences.PreferencesViewModel
import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/*
    -
        - Extension function that consider the application's lifecycle.
        -  Collect From Kotlin Flows While Considering the app Lifecycle In Android Jetpack Compose
            - If the app's lifecycle is ignored, it can lead to an unexpected behavior.
                - It can consume unnecessary resources.
*/

class ChatViewModel(
//) : ViewModel() {
    application: Application
) : AndroidViewModel(application) {

    var messages = mutableStateListOf<Message>()
        private set
    private var counter = MutableStateFlow(value = listOf<String>())
    //    private var _scrollState = rememberScrollState()
    var connectionState by mutableStateOf(String())
        private set
    private val newConnection by mutableStateOf(NewConnection())
    var commonInterests = mutableStateListOf<String>()
        private set

    // General user interests (saved in the settings DataStore, and the matching interests returned by Omegle's server)
//    val userInterests by PreferencesDataStore(context = application).getUserInterests().collectAsState(initial = listOf())
    private lateinit var _userInterests: MutableList<String>

    private val prohibitedIds = mutableStateListOf<String>()

    private var _textMessage = mutableStateOf("")
    val textMessage = _textMessage
//    var textMessageState by remember { mutableStateOf("") }

    fun sendTextMessage() {
        CoroutineScope(Dispatchers.IO).launch {
            newConnection.sendText(_textMessage.value)
        }
        messages.add(Message(0, _textMessage.value))
        connectionState = ConnectionStates.MESSAGE.state
        _textMessage.value = ""
    }

    fun terminate() {
        messages.clear()
        connectionState = ConnectionStates.DISCONNECTED.state
        commonInterests.clear()
        // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
        CoroutineScope(Dispatchers.IO).launch {
            newConnection.disconnect()  // This should be refined or something. I can't just sent a disconnect request at every single brand opening of the chatting screen
            newConnection.setCommonInterests(_userInterests)
            newConnection.start()
        }
    }

    private fun initializeObservers() {
        newConnection.setObserver(object : ConnectionObserver {
            override fun onConnected(usersCommonInterests: List<String>) {
                connectionState = ConnectionStates.CONNECTED.state
                commonInterests = usersCommonInterests.toMutableStateList()
                if (prohibitedIds.contains(newConnection.getClientId())) {
//                        Log.d("BLOCK", "Blocked ${newConnection.getClientId()}")
                    messages.clear()
                    connectionState = ConnectionStates.DISCONNECTED.state
                    commonInterests.clear()
                    // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
                    CoroutineScope(Dispatchers.IO).launch {
                        newConnection.disconnect()
                        newConnection.setCommonInterests(_userInterests.toMutableList())
                        newConnection.start()
                    }
                }
            }
            override fun onRecaptchaRequired() { connectionState = ConnectionStates.RECAPTCHA_REQUIRED.state }
            override fun onTyping() { connectionState = ConnectionStates.USER_TYPING.state }
            override fun onStoppedTyping() { connectionState = ConnectionStates.STALE.state }
            override fun onUserDisconnected() {
                connectionState = ConnectionStates.DISCONNECTED.state
                if (messages.size < 10) {       // Longer conversations should be preserved, in the case of leftover exchange information.
                    messages.clear()
                    commonInterests.clear()
                    newConnection.setCommonInterests(_userInterests.toMutableList())
                    CoroutineScope(Dispatchers.IO).launch {
                        newConnection.start()
                    }
                }
            }
            override fun onWaiting() { connectionState = ConnectionStates.WAITING.state }
            override fun onGotMessage(message: String) {
                connectionState = ConnectionStates.MESSAGE.state
                messages.add(Message(1, message))
//                    scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
            }
            override fun onError() { Log.d("ERROR", "Error Occurred") }
            override fun onEvent(response: String) { Log.d("ERROR", "Event!") }
        })
    }

    override fun onCleared() {
        super.onCleared()
        CoroutineScope(Dispatchers.IO).launch {
            newConnection.disconnect()
        }
    }

    init {
        _userInterests = PreferencesViewModel(application).userInterests
        initializeObservers()
    }
}

private enum class ConnectionStates(val state: String) {
    CONNECTED(state = "Connected"),
    DISCONNECTED(state = "Disconnected"),
    USER_TYPING(state = "User Typing"),
    MESSAGE(state = "Message"),
    WAITING(state = "Waiting"),
    RECAPTCHA_REQUIRED(state = "Recaptcha required!"),
    STALE(state = "Stale");
    override fun toString(): String = this.state
}
data class Message(
    val id: Int,
    val text: String
)