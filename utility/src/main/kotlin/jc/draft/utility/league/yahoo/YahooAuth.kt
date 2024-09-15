package jc.draft.utility.league.yahoo

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.headers
import io.ktor.util.InternalAPI
import jc.draft.utility.league.CacheableData
import jc.draft.utility.league.jsonParser
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class OauthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("xoauth_yahoo_guid") val xoauthYahooGuid: String? = null
)

data class YahooAuthConfig(
    val yahooClientId: String,
    val yahooClientSecret: String,
    val yahooAuthCode: String,
    val yahooRedirectUri: String
)

class YahooAuthService : CacheableData<YahooAuthConfig> {
    companion object {
        const val YAHOO_GET_TOKEN_URL = "https://api.login.yahoo.com/oauth2/get_token"
    }

    fun getYahooAccessToken(): OauthResponse {
        val config = YahooAuthConfig(
            yahooClientId = System.getenv("YAHOO_FANTASY_CLIENT_ID") ?: "",
            yahooClientSecret = System.getenv("YAHOO_FANTASY_CLIENT_SECRET") ?: "",
            yahooAuthCode = System.getenv("YAHOO_FANTASY_OAUTH_CODE") ?: "",
            yahooRedirectUri = "https://jc-draft-utility.com"
        )
        return parseAuthData(getData(config))
    }

    override fun directory(c: YahooAuthConfig): String {
        return "yahoo/oauth/${c.yahooClientId}"
    }

    // access token only valid for 1 hour
    override fun refreshDurationHours(): Long {
        return 1
    }

    override fun refreshDataFirstTime(c: YahooAuthConfig): String {
        return getYahooAccessToken(c, FormDataContent(Parameters.build {
            append("redirect_uri", c.yahooRedirectUri)
            append("client_id", c.yahooClientId)
            append("client_secret", c.yahooClientSecret)
            append("grant_type", "authorization_code")
            append("code", c.yahooAuthCode)
        }))
    }

    override fun refreshData(c: YahooAuthConfig, existingData: String): String {
        return getYahooAccessToken(c, FormDataContent(Parameters.build {
            append("redirect_uri", c.yahooRedirectUri)
            append("client_id", c.yahooClientId)
            append("client_secret", c.yahooClientSecret)
            append("grant_type", "refresh_token")
            append("refresh_token", getRefreshToken(existingData))
        }))
    }

    private fun getRefreshToken(existingData: String): String {
        return parseAuthData(existingData).refreshToken
    }

    private fun parseAuthData(data: String): OauthResponse {
        return jsonParser.decodeFromString<OauthResponse>(data)
    }

    @OptIn(ExperimentalEncodingApi::class, InternalAPI::class)
    private fun getYahooAccessToken(c: YahooAuthConfig, formData: FormDataContent): String {
        return runBlocking {
            val response: HttpResponse = HttpClient(CIO).request(YAHOO_GET_TOKEN_URL) {
                method = HttpMethod.Post
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Basic ${Base64.Default.encode("${c.yahooClientId}:${c.yahooClientSecret}".toByteArray())}"
                    )
                    append(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                }
                body = formData
            }

            when (response.status) {
                HttpStatusCode.OK -> println("successfully retrieved yahoo access token info")
                else -> {
                    println("failed to authorized with yahoo: \nstatus:${response.status}\nbody:${response.bodyAsText()}")
                    throw RuntimeException("failed to authorized with yahoo: \nstatus:${response.status}\nbody:${response.bodyAsText()}")
                }
            }

            return@runBlocking response.bodyAsText()
        }
    }

}