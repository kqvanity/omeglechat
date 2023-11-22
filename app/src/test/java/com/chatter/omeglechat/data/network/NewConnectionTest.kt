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
    private val threshold = 3
    private var counter = 0

    @Before
    fun setup() {
        coroutineIOScope = CoroutineScope(Dispatchers.IO)
        connection = ConnectionImpl()
    }

    @Test
    fun start() {
        connection.connectionObserver = object: ConnectionObserver {
            override fun onEvent(response: String) { }
            override fun onWaiting() {
                println("Waiting")
            }
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
            val messages = listOf("Hi", "How's life", "Hello")
            override fun onGotMessage(message: String) {
                println(">> ${message}")
                messages.random().let {
                    coroutineIOScope.launch {
                        println(">> ${it}")
                        connection.sendText(it)
                    }
                }
                counter++
                if (counter == threshold) coroutineIOScope.launch {
                    connection.disconnect()
                    return@launch
                }
            }
            override fun onUserDisconnected() {
                println("User disconnected")
                connection.clientId = ""
//                coroutineIOScope.launch {
//                    NewConnection.start()
//                }
            }
            override fun onError() {
                println("Error")
            }
            override fun onConnectionError() {
                println("Error connecting to server. Please try again.")
            }
        }
        runBlocking {
            connection.commonInterests.addAll(listOf("talk"))
            connection.start()
        }
    }

}