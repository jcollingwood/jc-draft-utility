package jc.draft.utility.league

enum class LeaguePlatform {
    ESPN,
    SLEEPER,
    YAHOO
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
    fun getLeagues(): List<LeagueConfig>
    fun getLeagueDataService(): CacheableData<LeagueConfig>
    fun parsePlayersFromPayload(payload: String, teamId: String): List<P>
    fun mapToFantasyPlayer(player: P): FantasyPlayer

    fun getLeaguePlayers(): List<FantasyLeague> {
        return getLeagues().map { league ->
            FantasyLeague(
                league = league,
                players = retrieveLeagueRoster(league)?.map(::mapToFantasyPlayer) ?: emptyList()
            )
        }
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
