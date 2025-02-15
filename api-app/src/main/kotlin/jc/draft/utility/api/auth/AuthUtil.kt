package jc.draft.utility.api.auth

import io.ktor.server.application.ApplicationCall
import io.ktor.server.sessions.sessions

suspend fun getUserSession(
    call: ApplicationCall
): UserSession? {
    val userSession: UserSession? = call.sessions.get(SESSION_COOKIE_KEY) as UserSession?
    //if there is no session, redirect to login
    if (userSession == null)
        throw UserAuthError()
    return userSession
}
