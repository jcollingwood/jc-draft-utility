package jc.draft.utility.api.auth

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.oauth
import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sessions
import jc.draft.utility.CacheableData
import jc.draft.utility.api.config.GOOGLE_CLIENT_ID
import jc.draft.utility.api.config.GOOGLE_CLIENT_SECRET
import jc.draft.utility.api.config.PORT
import jc.draft.utility.league.jsonParser
import kotlinx.coroutines.runBlocking
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val state: String, val token: String)

val OAUTH_KEY = "auth-oauth-google"

// TODO understand cookie stuff better
val SESSION_COOKIE_KEY = "sesh"

// TODO externalize domain
val ROOT_DOMAIN = "http://localhost:${PORT}"

// map of redirects
val redirects = mutableMapOf<String, String>()

// auth login flow
fun Application.authRouting() {
    routing {
        get("/") {
            call.respondHtml {
                body {
                    p {
                        a("/login") { +"Login with Google" }
                    }
                }
            }
        }
        get("/home") {
            val userSession: UserSession? = authenticate(call)
            if (userSession == null) return@get
            call.respondText("Token ${userSession.token}, state ${userSession.state}")
        }
        authenticate("auth-oauth-google") {
            get("/login") {
                // Redirects to 'authorizeUrl' automatically
            }

            get("/callback") {
                val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                // redirects home if the url is not found before authorization
                currentPrincipal?.let { principal ->
                    principal.state?.let { state ->
                        val userSession = UserSession(state, principal.accessToken)
                        call.sessions.set(SESSION_COOKIE_KEY, userSession)
                        redirects[state]?.let { redirect ->
                            call.respondRedirect(redirect)
                            return@get
                        }
                    }
                }
                call.respondRedirect("/home")
            }
        }
    }
}

fun Application.authModule(httpClient: HttpClient) {
    install(Sessions) {
        cookie<UserSession>(SESSION_COOKIE_KEY)
    }
    install(Authentication) {
        oauth(OAUTH_KEY) {
            // Configure oauth authentication
            urlProvider = { "${ROOT_DOMAIN}/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = GOOGLE_CLIENT_ID,
                    clientSecret = GOOGLE_CLIENT_SECRET,
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                    extraAuthParameters = listOf("access_type" to "offline"),
                    onStateCreated = { call, state ->
                        //saves new state with redirect url value
                        call.request.queryParameters["redirectUrl"]?.let {
                            redirects[state] = it
                        }
                    }
                )
            }
            client = httpClient
        }
    }
    authRouting()
}

@Serializable
data class UserInfo(
    val id: String,
    val name: String,
    @SerialName("given_name")
    val givenName: String,
    @SerialName("family_name")
    val familyName: String,
    val picture: String
)

class UserInfoService(
    val client: HttpClient,
    val userInfoCacheService: CacheableData<UserSession> = UserInfoCacheService(client)
) {
    fun getUserInfo(userSession: UserSession): UserInfo {
        return jsonParser.decodeFromString<UserInfo>(userInfoCacheService.lockedGetData(userSession))
    }
}

class UserInfoCacheService(val client: HttpClient) : CacheableData<UserSession> {
    override fun directory(c: UserSession): String {
        return c.token
    }

    override fun refreshDurationHours(): Long {
        return 1
    }

    override fun refreshData(c: UserSession, existingData: String): String {
        return runBlocking {
            client.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${c.token}")
                }
                // TODO add 401 redirect to reauth logic
            }.bodyAsText()
        }
    }
}
