package com.chatter.backend

import com.chatter.backend.plugins.configureRouting
import com.chatter.backend.plugins.configureSockets
import io.ktor.server.application.*

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureRouting()
    configureSockets()
}