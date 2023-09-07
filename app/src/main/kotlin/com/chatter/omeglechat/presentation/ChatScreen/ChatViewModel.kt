package com.chatter.omeglechat.ChatScreen

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import com.chatter.omeglechat.data.network.NewConnection
import com.chatter.omeglechat.domain.model.ConnectionStates
import com.chatter.omeglechat.preferences.PreferencesDataStore

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
    private lateinit var userInterests: MutableList<String>

    private val prohibitedIds = mutableStateListOf<String>()

    var textMessage = mutableStateOf("")

    fun sendTextMessage() {
        CoroutineScope(Dispatchers.IO).launch {
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
        // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
        CoroutineScope(Dispatchers.IO).launch {
            NewConnection.disconnect()  // This should be refined or something. I can't just sent a disconnect request at every single brand opening of the chatting screen
            NewConnection.setCommonInterests(userInterests)
            NewConnection.start()
            NewConnection.continueOn()
        }
    }

    private fun initializeObservers() {
        NewConnection.connectionObserver = object : ConnectionObserver {
            override fun onConnected(usersCommonInterests: List<String>) {
                connectionState = ConnectionStates.CONNECTED.displayName
                commonInterests = usersCommonInterests.toMutableStateList()
                if (prohibitedIds.contains(NewConnection.clientId)) {
                    messages.clear()
                    connectionState = ConnectionStates.DISCONNECTED.displayName
                    commonInterests.clear()
                    // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
                    CoroutineScope(Dispatchers.IO).launch {
                        NewConnection.disconnect()
                        NewConnection.setCommonInterests(userInterests.toMutableList())
                        NewConnection.start()
                    }
                }
            }
            override fun onRecaptchaRequired() { connectionState = ConnectionStates.RECAPTCHA_REQUIRED.displayName }
            override fun onTyping() { connectionState = ConnectionStates.TYPING.displayName }
            override fun onStoppedTyping() { connectionState = ConnectionStates.STALE.displayName }
            override fun onUserDisconnected() {
                connectionState = ConnectionStates.DISCONNECTED.displayName
                // Longer conversations should be preserved, in the case of leftover exchange information.
                if (messages.size < 10) {
                    messages.clear()
                    commonInterests.clear()
                    NewConnection.setCommonInterests(userInterests.toMutableList())
                    CoroutineScope(Dispatchers.IO).launch {
                        NewConnection.start()
                    }
                }
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
        CoroutineScope(Dispatchers.IO).launch {
            NewConnection.disconnect()
        }
    }

    init {
//        userInterests = PreferencesViewModel(application).userInterests
        userInterests = mutableListOf("talk")
        initializeObservers()
    }
}

data class Message(
    val id: Int,
    val text: String
)