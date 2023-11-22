package com.chatter.omeglechat.data.network

import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before

import org.junit.Test

class NewConnectionTest {

    private lateinit var coroutineIOScope: CoroutineScope
    private lateinit var connection: Connection

    @Before
    fun setup() {
        coroutineIOScope = CoroutineScope(Dispatchers.IO)
    }

    @Test
    fun start() {
        val messages = listOf("Hello!", "How are you doing?", "How's life?")
        connection = object : ConnectionImpl() {
            override val commonInterests: MutableList<String>
                get() = mutableListOf()
            override var clientId: String
                get() = ""
                set(value) {}
            override var connectionObserver: ConnectionObserver = object: ConnectionObserver {
                override fun onEvent(response: String) { }
                override fun onConnected(commonInterests: List<String>) {
                    println("Connected")
                }
                override fun onTyping() {
                    println("Typing")
                }
                override fun onStoppedTyping() {
                    println("Stopped typing")
                }
                override fun onRecaptchaRequired() {
                    println("Recaptcha required")
                }
                override fun onGotMessage(message: String) {
                    println(message)
                    coroutineIOScope.launch {
                        messages.random().let {
                            connection.sendText(it)
                            println(it)
                        }
                    }
                }
                override fun onUserDisconnected() {
                    println("User disconnected")
                }
                override fun onWaiting() {
                    println("Waiting")
                }
                override fun onError() {
                    println("Error")
                }
                override fun onConnectionError() {
                    println("Error connecting to server. Please try again.")
                }
            }
        }
        runBlocking {
            connection.commonInterests.addAll(listOf("talk"))
            connection.start()
        }
    }

}