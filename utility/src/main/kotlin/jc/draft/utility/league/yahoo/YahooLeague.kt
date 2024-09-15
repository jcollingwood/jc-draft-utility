package jc.draft.utility.league.yahoo

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import jc.draft.utility.league.CacheableData
import jc.draft.utility.league.FantasyPlatform
import jc.draft.utility.league.FantasyPlayer
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.client
import kotlinx.coroutines.runBlocking

val yahooClient = HttpClient(CIO) {
    install(Auth) {
        bearer {
            loadTokens { null }
            refreshTokens { null }
        }
    }
}

val ramLeague = LeagueConfig(
    leagueName = "Ram",
    leagueId = "",
    teamId = ""
)

/**
 * yahoo uses OAuth for authentication
 *
 * figuring out automation but can manually workaround with this url to get the code:
 * https://api.login.yahoo.com/oauth2/request_auth\?client_id\=$YAHOO_FANTASY_CLIENT_ID\&response_type\=code\&redirect_uri\=https://jc-draft-utility.com
 */
class YahooFantasyPlatform(
    val leagueConfigs: List<LeagueConfig>,
    private val yahooLeague: CacheableData<LeagueConfig> = YahooLeagueData(yahooAuthService = YahooAuthService())
) : FantasyPlatform<String> {
    override fun getLeagues(): List<LeagueConfig> {
        return leagueConfigs
    }

    override fun getLeagueDataService(): CacheableData<LeagueConfig> {
        return yahooLeague
    }

    override fun parsePlayersFromPayload(
        payload: String,
        teamId: String
    ): List<String> {
        TODO("Not yet implemented")
    }

    override fun mapToFantasyPlayer(player: String): FantasyPlayer {
        TODO("Not yet implemented")
    }
}

class YahooLeagueData(
    private val yahooAuthService: YahooAuthService
) : CacheableData<LeagueConfig> {
    private fun getYahooUrl(c: LeagueConfig): String {
        return "https://fantasysports.yahooapis.com/fantasy/v2/team/${c.teamId}"
    }

    override fun directory(c: LeagueConfig): String {
        return "${c.year}_${c.leagueName}_${c.leagueId}"
    }

    override fun refreshData(c: LeagueConfig, existingData: String): String {
        val accessToken = yahooAuthService.getYahooAccessToken().accessToken
        return runBlocking {
            val response: HttpResponse = client.request("${getYahooUrl(c)}/roster") {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Accept, "application/json")
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> println("successfully retrieved yahoo league roster data")
                else -> println("failed to retrieve yahoo league roster data : \nstatus:${response.status}\nbody:${response.bodyAsText()}")
            }
            return@runBlocking response.bodyAsText()
        }
    }
}