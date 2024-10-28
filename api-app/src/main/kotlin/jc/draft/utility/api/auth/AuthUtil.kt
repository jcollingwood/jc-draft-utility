package jc.draft.utility.api.auth

import io.ktor.http.URLBuilder
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.uri
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.sessions

suspend fun authenticate(
    call: ApplicationCall
): UserSession? {
    val userSession: UserSession? = call.sessions.get(SESSION_COOKIE_KEY) as UserSession?
    //if there is no session, redirect to login
    if (userSession == null) {
        val redirectUrl = URLBuilder("${ROOT_DOMAIN}/login").run {
            parameters.append("redirectUrl", call.request.uri)
            build()
        }
        call.respondRedirect(redirectUrl)
        return null
    }
    return userSession
}
