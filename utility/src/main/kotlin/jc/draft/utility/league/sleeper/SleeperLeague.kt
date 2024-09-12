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

class SleeperFantasyPlatform : FantasyPlatform<SleeperPlayer> {
    private val sleeperLeague = SleeperLeagueData()
    private val sleeperPlayers = getSleeperPlayers()

    override fun getLeagues(): List<LeagueConfig> {
        return listOf(ffbCardsLeague, famanticsLeague, bellmanLeague, ffbDynastyLeague)
    }

    override fun getLeagueDataService(): CacheableData<LeagueConfig> {
        return sleeperLeague
    }

    override fun parsePlayersFromJson(json: String, teamId: String): List<SleeperPlayer> {
        return jsonParser.decodeFromString<List<SleeperLeagueRoster>>(json)
            .filter { teamId == it.owner_id }.first().players
            .map { sleeperPlayers?.get(it) ?: SleeperPlayer() }
    }

    override fun mapToFantasyPlayer(player: SleeperPlayer): FantasyPlayer {
        return FantasyPlayer(
            fullName = player.first_name + " " + player.last_name,
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

    override fun refreshData(c: LeagueConfig): String {
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