package jc.draft.utility.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import mu.two.KotlinLogging


private val log = KotlinLogging.logger {}

fun Application.errorHandler() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            log.error("unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, "oops...")
        }
    }
}