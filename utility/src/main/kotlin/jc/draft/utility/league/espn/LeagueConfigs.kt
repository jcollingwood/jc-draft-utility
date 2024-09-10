package jc.draft.utility.league.espn

data class LeagueConfig(
    val leagueName: String,
    val leagueId: Int,
    val year: Int = 2024,
    val teamId: Int
)

val federationLeague: LeagueConfig = LeagueConfig(
    leagueName = "Federation",
    leagueId = 2138393078,
    teamId = 12
)

val bfflLeague: LeagueConfig = LeagueConfig(
    leagueName = "BFFL",
    leagueId = 522338,
    teamId = 8
)

val workLeague: LeagueConfig = LeagueConfig(
    leagueName = "Work",
    leagueId = 80220384,
    teamId = 3
)

val clayLeague: LeagueConfig = LeagueConfig(
    leagueName = "Clay",
    leagueId = 215434,
    teamId = 21
)

