package jc.draft.utility.api

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun Application.module() {
    configureDatabase()
    configureRouting()
}

fun main() {
    embeddedServer(Netty, 8081) {
        module()
    }.start(wait = true)
}
