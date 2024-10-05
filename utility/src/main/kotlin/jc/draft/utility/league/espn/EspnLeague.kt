package jc.draft.utility.league.espn

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import jc.draft.utility.league.CacheDataType
import jc.draft.utility.league.CacheableData
import jc.draft.utility.league.FantasyPlatform
import jc.draft.utility.league.FantasyPlayer
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.client
import jc.draft.utility.league.jsonParser
import kotlinx.coroutines.runBlocking

/**
 * ESPN uses cookies for authentication
 */
class EspnFantasyPlatform(
    private val espnLeague: CacheableData<LeagueConfig> = EspnLeagueData()
) : FantasyPlatform<EspnPlayer> {

    override fun getLeagueDataService(): CacheableData<LeagueConfig> {
        return espnLeague
    }

    override fun parsePlayersFromPayload(payload: String, teamId: String): List<EspnPlayer> {
        return jsonParser.decodeFromString<EspnResponse>(payload)
            .teams.filter { it.id.toString() == teamId }
            .first().roster?.entries?.map { it.playerPoolEntry?.player }?.filterNotNull() ?: emptyList()
    }

    override fun mapToFantasyPlayer(player: EspnPlayer): FantasyPlayer {
        return FantasyPlayer(
            isStarting = false,
            fullName = player.fullName,
            position = getEspnPosition(player.defaultPositionId),
            status = player.injuryStatus?.lowercase()?.capitalize()
        )
    }
}

class EspnLeagueData : CacheableData<LeagueConfig> {
    companion object {
        const val ESPN_URL = "https://lm-api-reads.fantasy.espn.com/apis/v3/games"
    }

    // espn cookies (can be found in Chrome under application/cookies with same name)
    // note: remove surrounding {} from swid
    // note: ensure you grab url encoded value (or url encode in code here)
    val SWID = System.getenv("ESPN_SWID")
    val ESPN_S2 = System.getenv("ESPN_S2")

    private fun getLeagueUrl(config: LeagueConfig): String {
        return "${ESPN_URL}/ffl/seasons/${config.year}/segments/0/leagues/${config.leagueId}"
    }

    override fun directory(c: LeagueConfig): String {
        return "${c.year}_${c.leagueName}_${c.leagueId}"
    }

    override fun dataType(): CacheDataType {
        return CacheDataType.JSON
    }

    /* make api call to retrieve league roster data */
    override fun refreshData(c: LeagueConfig, existingData: String): String {
        return runBlocking {
            val response: HttpResponse = client.request(getLeagueUrl(c)) {
                method = HttpMethod.Get
                url {
                    parameters.append("rosterForTeamId", c.teamId)
                    parameters.append("view", EspnView.Roster.value)
                }
                cookie("swid", SWID)
                cookie("espn_s2", ESPN_S2)
            }
            when (response.status) {
                HttpStatusCode.OK -> println("successfully retrieved league roster data")
                else -> {
                    val errorMessage =
                        "failed to retrieve league roster data : \nstatus:${response.status}\nbody:${response.bodyAsText()}"
                    println(errorMessage)
                    throw RuntimeException(errorMessage)
                }
            }
            return@runBlocking response.bodyAsText()
        }
    }
}
