package jc.draft.utility.league.yahoo

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import jc.draft.utility.league.CacheDataType
import jc.draft.utility.league.CacheableData
import jc.draft.utility.league.FantasyPlatform
import jc.draft.utility.league.FantasyPlayer
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.LeaguePlatform
import jc.draft.utility.league.client
import kotlinx.coroutines.runBlocking

val ramLeague = LeagueConfig(
    leaguePlatform = LeaguePlatform.YAHOO,
    leagueName = "Ram",
    leagueId = "426660",
    teamId = "3"
)

// jackson xml mapper config for kotlin
val xmlDeserializer = XmlMapper(JacksonXmlModule()
    .apply { setDefaultUseWrapper(false) })
    .registerKotlinModule()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

/**
 * yahoo uses OAuth for authentication
 *
 * figuring out automation but can manually workaround with this url to get the code:
 * https://api.login.yahoo.com/oauth2/request_auth\?client_id\=$YAHOO_FANTASY_CLIENT_ID\&response_type\=code\&redirect_uri\=https://jc-draft-utility.com
 */
class YahooFantasyPlatform(
    private val yahooLeague: CacheableData<LeagueConfig> = YahooLeagueData(yahooAuthService = YahooAuthService())
) : FantasyPlatform<YahooPlayer> {

    override fun getLeagueDataService(): CacheableData<LeagueConfig> {
        return yahooLeague
    }

    override fun parsePlayersFromPayload(
        payload: String,
        teamId: String
    ): List<YahooPlayer> {
        val yahooRoster = xmlDeserializer.readValue(payload, YahooFantasyContent::class.java)
        return yahooRoster.team.roster.players
    }

    override fun mapToFantasyPlayer(player: YahooPlayer): FantasyPlayer {
        return FantasyPlayer(
            // is starting if selected position isn't BN (bench) or IR
            isStarting = !listOf("BN", "IR").contains(player.selectedPosition.position),
            fullName = player.name.full,
            position = getYahooPosition(player.primaryPosition),
            status = getYahooStatus(player.status)
        )
    }
}

class YahooLeagueData(
    private val yahooAuthService: YahooAuthService
) : CacheableData<LeagueConfig> {
    private fun constructTeamKey(c: LeagueConfig): String {
        return "nfl.l.${c.leagueId}.t.${c.teamId}"
    }

    private fun getYahooUrl(c: LeagueConfig): String {
        return "https://fantasysports.yahooapis.com/fantasy/v2/team/${constructTeamKey(c)}"
    }

    override fun directory(c: LeagueConfig): String {
        return "${c.year}_${c.leagueName}_${c.leagueId}"
    }

    override fun dataType(): CacheDataType {
        return CacheDataType.XML
    }

    override fun refreshData(c: LeagueConfig, existingData: String): String {
        val accessToken = yahooAuthService.getYahooAccessToken().accessToken
        return runBlocking {
            val url = "${getYahooUrl(c)}/roster"
            val response: HttpResponse = client.request(url) {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> println("successfully retrieved yahoo league roster data")
                else -> {
                    val errorMessage =
                        "failed to retrieve yahoo league roster data : \nstatus:${response.status}\nbody:${response.bodyAsText()}"
                    println(errorMessage)
                    throw RuntimeException(errorMessage)
                }
            }
            return@runBlocking response.bodyAsText()
        }
    }
}