package jc.draft.utility.league

data class LeagueConfig(
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
    fun parsePlayersFromJson(json: String, teamId: String): List<P>
    fun mapToFantasyPlayer(player: P): FantasyPlayer

    fun getPlayers(): List<FantasyLeague> {
        return getLeagues().map { league ->
            FantasyLeague(
                league = league,
                players = retrieveLeagueRoster(league)?.map(::mapToFantasyPlayer) ?: emptyList()
            )
        }
    }

    fun retrieveLeagueRoster(leagueConfig: LeagueConfig): List<P>? {
        return parsePlayersFromJson(getLeagueDataService().getData(leagueConfig), leagueConfig.teamId)
    }
}
