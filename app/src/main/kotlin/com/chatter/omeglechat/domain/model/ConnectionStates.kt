package com.chatter.omeglechat.domain.model

enum class ConnectionStates(val state: String, val displayName: String) {
    CONNECTED("connected", "Connected"),
    DISCONNECTED("strangerDisconnected", "Disconnected"),
    TYPING("typing", "User Typing"),
    STOPPED_TYPING("stoppedTyping", "stopped Typing"),
    MESSAGE("gotMessage", "Message"),
    WAITING("waiting", "Waiting"),
    RECAPTCHA_REQUIRED("recaptchaRequired", "Recaptcha required!"),
    ERROR("error", "error"),
    CONNECTION_ERROR("Connection Error", "Connection Error"),
    STALE("stoppedTyping","Stale");
    override fun toString(): String = this.state
}
