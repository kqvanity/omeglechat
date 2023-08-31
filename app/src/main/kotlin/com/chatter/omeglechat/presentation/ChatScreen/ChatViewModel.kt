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

    private var _messages = mutableStateListOf<Message>()
    private var counter = MutableStateFlow(value = listOf<String>())
    //    private var _scrollState = rememberScrollState()
    private var _connectionState by mutableStateOf(String())
    private var _newConnection by mutableStateOf(NewConnection())
    private var _commonInterests = mutableStateListOf<String>()
    // General user interests (saved in the settings DataStore, and the matching interests returned by Omegle's server)
//    val userInterests by PreferencesDataStore(context = application).getUserInterests().collectAsState(initial = listOf())
    private lateinit var _userInterests: MutableList<String>

    private val _prohibitedIds = mutableStateListOf<String>()

    private var _textMessage = mutableStateOf("")
    val textMessage = _textMessage
//    var textMessageState by remember { mutableStateOf("") }

    fun sendTextMessage() {
        CoroutineScope(Dispatchers.IO).launch {
            _newConnection.sendText(_textMessage.value)
        }
        _messages.add(Message(0, _textMessage.value))
        _connectionState = ConnectionStates.MESSAGE.state
        _textMessage.value = ""
    }

    fun terminate() {
        _messages.clear()
        _connectionState = ConnectionStates.DISCONNECTED.state
        _commonInterests.clear()
        // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
        CoroutineScope(Dispatchers.IO).launch {
            _newConnection.disconnect()  // This should be refined or something. I can't just sent a disconnect request at every single brand opening of the chatting screen
            _newConnection.setCommonInterests(_userInterests)
            _newConnection.start()
        }
    }

    fun initializeObservers() {
        _newConnection.setObserver(object : ConnectionObserver {
            override fun onConnected(usersCommonInterests: List<String>) {
                _connectionState = ConnectionStates.CONNECTED.state
                _commonInterests = usersCommonInterests.toMutableStateList()
                if (_prohibitedIds.contains(_newConnection.getClientId())) {
//                        Log.d("BLOCK", "Blocked ${newConnection.getClientId()}")
                    _messages.clear()
                    _connectionState = ConnectionStates.DISCONNECTED.state
                    _commonInterests.clear()
                    // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
                    CoroutineScope(Dispatchers.IO).launch {
                        _newConnection.disconnect()
                        _newConnection.setCommonInterests(_userInterests.toMutableList())
                        _newConnection.start()
                    }
                }
            }
            override fun onRecaptchaRequired() { _connectionState = ConnectionStates.RECAPTCHA_REQUIRED.state }
            override fun onTyping() { _connectionState = ConnectionStates.USER_TYPING.state }
            override fun onStoppedTyping() { _connectionState = ConnectionStates.STALE.state }
            override fun onUserDisconnected() {
                _connectionState = ConnectionStates.DISCONNECTED.state
                if (_messages.size < 10) {       // Longer conversations should be preserved, in the case of leftover exchange information.
                    _messages.clear()
                    _commonInterests.clear()
                    _newConnection.setCommonInterests(_userInterests.toMutableList())
                    CoroutineScope(Dispatchers.IO).launch {
                        _newConnection.start()
                    }
                }
            }
            override fun onWaiting() { _connectionState = ConnectionStates.WAITING.state }
            override fun onGotMessage(message: String) {
                _connectionState = ConnectionStates.MESSAGE.state
                _messages.add(Message(1, message))
//                    scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
            }
            override fun onError() { Log.d("ERROR", "Error Occurred") }
            override fun onEvent(response: String) { Log.d("ERROR", "Event!") }
        })
    }

    var connectionState
        get() = _connectionState
        set(value) {
            _connectionState = value
        }

    var messages
        get() = _messages
        set(value) { _messages = value }

    var commonInterests
        get() = _commonInterests
        set(value) { _commonInterests = value }

    override fun onCleared() {
        super.onCleared()
        CoroutineScope(Dispatchers.IO).launch {
            _newConnection.disconnect()
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