package com.chatter.omeglechat.data.network

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
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
//                if (!message.equals("moree")) {
//                    coroutineIOScope.launch {
//                        NewConnection.disconnect()
//                        NewConnection.start()
//                        NewConnection.continueOn()
//                    }
//                }
                messages.random().let {
                    coroutineIOScope.launch {
                        println(">> ${it}")
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
            NewConnection.setCommonInterests(mutableListOf("gayming"))
            NewConnection.start()
            runBlocking {
                NewConnection.continueOn()
            }
        }
        val scanner = Scanner(System.`in`)
    }

    @Test
    fun `test more`() {
        val value = "[['value'], ['property', 'value']]"
        Gson().fromJson(value, JsonArray::class.java).forEach {
            (it as JsonArray)
                .asList()
                .map { it.asString }
                .run { println(it.toMutableList()) }
        }
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


