package com.chatter.omeglechat.data.network

import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import org.junit.Test
import java.io.InputStream
import java.util.Scanner

class NewConnectionTest {
    val coroutineIOScope = CoroutineScope(Dispatchers.IO)

    @Test
    fun start() {
        NewConnection.connectionObserver = object: ConnectionObserver {
            override fun onWaiting() {
                println("Waiting")
            }

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
            val messages = listOf("Hi", "How's life", "Hello")
            override fun onGotMessage(message: String) {
                println(">> ${message}")
                messages.random().let {
                    coroutineIOScope.launch {
                        println(">> ${it}")
                        NewConnection.sendText(it)
                    }
                }
            }
            override fun onUserDisconnected() {
                println("User disconnected")
                NewConnection.clientId = ""
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
            NewConnection.setCommonInterests(mutableListOf("talk"))
            NewConnection.start()
        }
    }

}
fun testUserInput(input: InputStream) {
    println("Input name")
//        val scanner = BufferedReader(InputStreamReader(System.`in`))
//        val value = scanner.readLine()
    val value = input.read()
    println(value)
}


