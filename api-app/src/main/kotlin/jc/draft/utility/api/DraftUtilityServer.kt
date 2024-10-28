package jc.draft.utility.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import jc.draft.utility.api.auth.authModule

fun Application.appModule() {
    configureDatabase()
    configureRouting()
}

val applicationHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

fun main() {
    val httpClient: HttpClient = applicationHttpClient
    embeddedServer(Netty, 8081) {
        authModule(httpClient)
        appModule()
    }.start(wait = true)
}
