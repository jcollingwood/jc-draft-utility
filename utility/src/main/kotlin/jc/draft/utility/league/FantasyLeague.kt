package jc.draft.utility.league

import jc.draft.utility.league.espn.EspnFantasyPlatform
import jc.draft.utility.league.espn.bfflLeague
import jc.draft.utility.league.espn.clayLeague
import jc.draft.utility.league.espn.federationLeague
import jc.draft.utility.league.espn.workLeague
import jc.draft.utility.league.sleeper.SleeperFantasyPlatform
import jc.draft.utility.league.sleeper.bellmanLeague
import jc.draft.utility.league.sleeper.famanticsLeague
import jc.draft.utility.league.sleeper.ffbCardsLeague
import jc.draft.utility.league.sleeper.ffbDynastyLeague
import jc.draft.utility.league.yahoo.YahooFantasyPlatform
import jc.draft.utility.league.yahoo.ramLeague

val fantasyLeagues = listOf(
    federationLeague,
    bfflLeague,
    clayLeague,
    workLeague,
    ffbCardsLeague,
    famanticsLeague,
    bellmanLeague,
    ffbDynastyLeague,
    ramLeague
)

enum class LeaguePlatform {
    ESPN,
    SLEEPER,
    YAHOO
}

fun fantasyPlatformFactory(leaguePlatform: LeaguePlatform): FantasyPlatform<*> {
    return when (leaguePlatform) {
        LeaguePlatform.ESPN -> EspnFantasyPlatform()
        LeaguePlatform.SLEEPER -> SleeperFantasyPlatform()
        LeaguePlatform.YAHOO -> YahooFantasyPlatform()
    }
}

data class LeagueConfig(
    val leaguePlatform: LeaguePlatform,
    val leagueName: String,
    val leagueId: String,
    val year: Int = 2024,
    val teamId: String
)

data class FantasyPlayer(
    val fullName: String,
    val position: String? = null,
    val status: String? = null
)

data class FantasyLeague(
    val league: LeagueConfig,
    val players: List<FantasyPlayer>
)

interface FantasyPlatform<P> {
    fun getLeagueDataService(): CacheableData<LeagueConfig>
    fun parsePlayersFromPayload(payload: String, teamId: String): List<P>
    fun mapToFantasyPlayer(player: P): FantasyPlayer

    fun getLeaguePlayers(leagueConfig: LeagueConfig): FantasyLeague {
        return FantasyLeague(
            league = leagueConfig,
            players = retrieveLeagueRoster(leagueConfig)?.map(::mapToFantasyPlayer) ?: emptyList()
        )
    }

    fun retrieveLeagueRoster(leagueConfig: LeagueConfig): List<P>? {
        try {
            return parsePlayersFromPayload(getLeagueDataService().getData(leagueConfig), leagueConfig.teamId)
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }
}
