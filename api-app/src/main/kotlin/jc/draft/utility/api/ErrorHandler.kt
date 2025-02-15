package jc.draft.utility.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import jc.draft.utility.api.auth.UserAuthError
import mu.two.KotlinLogging


private val log = KotlinLogging.logger {}

fun Application.errorHandler() {
    install(StatusPages) {
        exception<UserAuthError> { call, cause ->
            log.error("user not authorized", cause)
            // triggers login if user is not logged in or session is expired
            call.respondRedirect("/login")
        }
        exception<Throwable> { call, cause ->
            log.error("unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, "oops...")
        }
    }
}