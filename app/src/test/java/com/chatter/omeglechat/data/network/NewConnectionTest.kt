package com.chatter.omeglechat.data.network

import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import okhttp3.Response

import org.junit.Test
import java.io.InputStream
import java.util.Scanner

class NewConnectionTest {
    val coroutineIOScope = CoroutineScope(Dispatchers.IO)

    @Test
    fun start() {
        val messages = listOf("How are you doing", "What are you doing rn", "F", "I'm horny how about you")
        NewConnection.connectionObserver = object: ConnectionObserver {
            override fun onWaiting() {
                println("Waiting")
            }

            override fun onEvent(response: String) {

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
            override fun onGotMessage(message: String) {
                println(">> ${message}")
                messages.random().let {
                    println(">> ${it}")
                    coroutineIOScope.launch {
                        NewConnection.sendText(it)
                    }
                }
            }
            override fun onUserDisconnected() {
                println("User disconnected")
                coroutineIOScope.launch {
                    NewConnection.start()
                }
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
//            println(p)
            NewConnection.start()
            runBlocking {
                NewConnection.continueOn()
            }
        }
        val scanner = Scanner(System.`in`)
    }

    @Test
    fun `should do`() {
        testUserInput(System.`in`)
    }

}
fun testUserInput(input: InputStream) {
    println("Input name")
//        val scanner = BufferedReader(InputStreamReader(System.`in`))
//        val value = scanner.readLine()
    val value = input.read()
    println(value)
}


