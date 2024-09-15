package jc.draft.utility.league.sleeper

import io.ktor.client.request.request
import io.ktor.client.statement.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.http.HttpMethod
import jc.draft.utility.league.CacheableData
import jc.draft.utility.league.FantasyPlatform
import jc.draft.utility.league.FantasyPlayer
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.client
import jc.draft.utility.league.jsonParser
import kotlinx.coroutines.runBlocking

/**
 * Sleeper is public and readonly so no auth needed
 */
class SleeperFantasyPlatform(
    val leagueConfigs: List<LeagueConfig>,
    private val sleeperLeague: CacheableData<LeagueConfig> = SleeperLeagueData(),
    private val sleeperPlayers: Map<String, SleeperPlayer>? = getSleeperPlayers()
) : FantasyPlatform<SleeperPlayer> {
    override fun getLeagues(): List<LeagueConfig> {
        return leagueConfigs
    }

    override fun getLeagueDataService(): CacheableData<LeagueConfig> {
        return sleeperLeague
    }

    override fun parsePlayersFromPayload(payload: String, teamId: String): List<SleeperPlayer> {
        return jsonParser.decodeFromString<List<SleeperLeagueRoster>>(payload)
            .filter { teamId == it.ownerId }.first().players
            // sleeper roster data only includes player ids, get player details from global player list
            .map { sleeperPlayers?.get(it) ?: SleeperPlayer() }
    }

    override fun mapToFantasyPlayer(player: SleeperPlayer): FantasyPlayer {
        return FantasyPlayer(
            fullName = player.firstName + " " + player.lastName,
            // global player list shows Active for all player statuses... figure out
            status = player.status
        )
    }
}

class SleeperLeagueData : CacheableData<LeagueConfig> {
    private fun getSleeperLeagueUrl(config: LeagueConfig): String {
        return "https://api.sleeper.app/v1/league/${config.leagueId}/rosters"
    }

    override fun directory(c: LeagueConfig): String {
        return "${c.year}_${c.leagueName}_${c.leagueId}"
    }

    override fun refreshData(c: LeagueConfig, existingData: String): String {
        return runBlocking {
            val response: HttpResponse = client.request(getSleeperLeagueUrl(c)) {
                method = HttpMethod.Get
            }
            when (response.status) {
                HttpStatusCode.OK -> println("successfully retrieved league roster data")
                else -> println("failed to retrieve league roster data : \nstatus:${response.status}\nbody:${response.bodyAsText()}")
            }
            return@runBlocking response.bodyAsText()
        }
    }
}