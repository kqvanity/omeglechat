package com.chatter.omeglechat.presentation.ChatScreen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatter.omeglechat.ChatScreen.ChatViewModel
import com.chatter.omeglechat.data.network.ConnectionImpl
import com.chatter.omeglechat.domain.model.ConnectionStates
import com.chatter.omeglechat.domain.model.Message
import com.chatter.omeglechat.preferences.PreferencesRepository
import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date
import com.chatter.omeglechat.presentation.preferencesScreen.dataStore

class ChatViewModelImpl(
    application: Application = Application(),
) : ChatViewModel, AndroidViewModel(application) {
    private lateinit var connection: ConnectionImpl
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
            messages.add(Message(0, textMessage.value, Date.from(Instant.now())))
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
            connection.commonInterests.clear()
            connection.commonInterests.addAll(userInterests)
            connection.start()
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
        connection = object: ConnectionImpl() {
            override suspend fun sendText(message: String) {
                super.sendText(message)
            }
            override val commonInterests: MutableList<String>
                get() = mutableListOf()
            override fun disconnect() {
                super.disconnect()
            }
            override var connectionObserver: ConnectionObserver = object : ConnectionObserver {
                override fun onConnected(usersCommonInterests: List<String>) {
                    connectionState = ConnectionStates.CONNECTED.displayName
                    commonInterests.addAll(usersCommonInterests.toMutableStateList())
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
                    connection.commonInterests.addAll(userInterests)
                    connection.clientId = ""
                }
                override fun onConnectionError() { }
                override fun onWaiting() { connectionState = ConnectionStates.WAITING.displayName }
                override fun onGotMessage(message: String) {
                    connectionState = ConnectionStates.MESSAGE.displayName
                    messages.add(Message(id = 1, text =  message, date = Date.from(Instant.now())))
                }
                override fun onError() { Log.d("ERROR", "Error Occurred") }
                override fun onEvent(response: String) { Log.d("ERROR", "Event!") }
            }
            override var clientId: String
                get() = ""
                set(value) {}
        }
        viewModelScope.launch {
            val preferencesRepository = PreferencesRepository(application.applicationContext.dataStore)
            userInterests = preferencesRepository.getUserInterests().first()
            connection.start()
            connection.commonInterests.addAll(userInterests.toMutableList())
        }
    }

}