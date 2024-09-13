package jc.draft.utility.league.yahoo

import jc.draft.utility.league.CacheableData
import jc.draft.utility.league.FantasyPlatform
import jc.draft.utility.league.FantasyPlayer
import jc.draft.utility.league.LeagueConfig

val ramLeague = LeagueConfig(
    leagueName = "Ram",
    leagueId = "",
    teamId = ""
)

/**
 * yahoo uses OAuth for authentication
 */
class YahooFantasyPlatform(
    val leagues: List<LeagueConfig>,
    private val yahooLeague: CacheableData<LeagueConfig> = YahooLeagueData()
) : FantasyPlatform<String> {
    override fun getLeagues(): List<LeagueConfig> {
        return leagues
    }

    override fun getLeagueDataService(): CacheableData<LeagueConfig> {
        return yahooLeague
    }

    override fun parsePlayersFromJson(
        json: String,
        teamId: String
    ): List<String> {
        TODO("Not yet implemented")
    }

    override fun mapToFantasyPlayer(player: String): FantasyPlayer {
        TODO("Not yet implemented")
    }
}

class YahooLeagueData : CacheableData<LeagueConfig> {
    override fun directory(c: LeagueConfig): String {
        return "${c.year}_${c.leagueName}_${c.leagueId}"
    }

    override fun refreshData(c: LeagueConfig): String {
        TODO("Not yet implemented")
    }
}