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

enum class LeaguePlatform(val displayValue: String) {
    ESPN("Espn"),
    SLEEPER("Sleeper"),
    YAHOO("Yahoo")
}

// order of enum will affect ordering of list
enum class Position {
    QB,
    RB,
    WR,
    TE,
    K,
    DST,
    Unknown
}

enum class Status(val displayValue: String) {
    Active("Active"),
    Questionable("Quest."),
    Doubtful("Doubt."),
    Out("Out"),
    PUP("PUP"),
    IR("IR"),
    Unknown("???")
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
) {
    val leagueUrl: String
        get() = when (leaguePlatform) {
            LeaguePlatform.ESPN -> "https://fantasy.espn.com/football/team?leagueId=$leagueId&teamId=$teamId&seasonId=$year"
            LeaguePlatform.SLEEPER -> "https://sleeper.com/leagues/$leagueId/team"
            LeaguePlatform.YAHOO -> "https://football.fantasysports.yahoo.com/f1/$leagueId/$teamId"
        }
}

data class FantasyPlayer(
    val isStarting: Boolean = false,
    val fullName: String,
    val position: Position = Position.Unknown,
    val status: Status = Status.Unknown
)

data class FantasyLeague(
    val league: LeagueConfig,
    val players: List<FantasyPlayer>
)

interface FantasyPlatform<P> {
    fun getLeagueDataService(): CacheableData<LeagueConfig>
    fun parsePlayersFromPayload(payload: String, teamId: String): List<P>
    fun mapToFantasyPlayer(player: P): FantasyPlayer

    fun getLeaguePlayers(leagueConfig: LeagueConfig, fetchNew: Boolean = false): FantasyLeague {
        return FantasyLeague(
            league = leagueConfig,
            players = retrieveLeagueRoster(leagueConfig, fetchNew)?.map(::mapToFantasyPlayer)
                // sort by starting first, then by position
                ?.sortedWith(compareBy({ !it.isStarting }, { it.position })) ?: emptyList()
        )
    }

    fun retrieveLeagueRoster(leagueConfig: LeagueConfig, fetchNew: Boolean): List<P>? {
        try {
            return parsePlayersFromPayload(getLeagueDataService().getData(leagueConfig, fetchNew), leagueConfig.teamId)
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }
}
