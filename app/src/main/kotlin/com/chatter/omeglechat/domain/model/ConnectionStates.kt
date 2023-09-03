package com.chatter.omeglechat.domain.model

enum class ConnectionStates(val state: String, val displayName: String) {
    CONNECTED("connected", "Connected"),
    DISCONNECTED("strangerDisconnected", "Disconnected"),
    TYPING("typing", "User Typing"),
    STOPPED_TYPING("stoppedTyping", "stoppedTyping"),
    MESSAGE("gotMessage", "Message"),
    WAITING("waiting", "Waiting"),
    RECAPTCHA_REQUIRED("recaptchaRequired", "Recaptcha required!"),
    ERROR("error", "error"),
    STALE("stoppedTyping","Stale");
    override fun toString(): String = this.state
}
