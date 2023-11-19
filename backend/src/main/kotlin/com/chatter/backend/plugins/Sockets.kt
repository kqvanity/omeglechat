package com.chatter.backend.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
    }
    routing {
        webSocket("/chat") {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    println(text)
                    outgoing.send(Frame.Text("You said ${text}"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE!"))
                    }
                }
            }
        }
    }
}