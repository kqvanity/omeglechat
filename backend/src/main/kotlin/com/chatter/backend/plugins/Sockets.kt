package com.chatter.backend.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import java.time.Duration
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val user = "user-${lastId.getAndIncrement()}"
}

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
    }
    routing {
        val connectionGroups = Collections.synchronizedSet<MutableList<Connection>>(LinkedHashSet())
        webSocket("/chat") {
            connectionGroups.filter { it.isEmpty() || it.size == 1 }.run {
                if (isEmpty()) {
                    connectionGroups.add(mutableListOf(Connection(this@webSocket)))
                } else {
                    this.first().add(Connection(session = this@webSocket))
                }
            }
//            send(Frame.Text("You're connected! There's are ${pairConnections.count()} users here!"))
            val currentGroup = connectionGroups.find { it.find { it.session == this } != null }
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                            currentGroup?.forEach {
                            if (it.session != this) {
                                it.session.send(Frame.Text(text))
                            }
                        }
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE!"))
                        }
                    }
                }
            } finally {
                currentGroup?.removeIf { it.session == this }
            }
        }
    }
}