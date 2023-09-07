package com.polendina.lib

interface ConnectionObserver {
    fun onEvent(response: String)
    fun onConnected(commonInterests: List<String>)
    fun onTyping()
    fun onStoppedTyping()
    fun onRecaptchaRequired()
    fun onGotMessage(message: String)
    fun onUserDisconnected()
    fun onWaiting()
    fun onError()
    fun onConnectionError()
}