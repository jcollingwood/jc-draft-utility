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

fun main() {
    val espnPlatform = EspnFantasyPlatform(leagueConfigs = listOf(federationLeague, bfflLeague, clayLeague, workLeague))
    val sleeperPlatform =
        SleeperFantasyPlatform(leagueConfigs = listOf(ffbCardsLeague, famanticsLeague, bellmanLeague, ffbDynastyLeague))
    val yahooPlatform = YahooFantasyPlatform(leagueConfigs = listOf(ramLeague))

    val fantasyPlatforms = listOf(
        espnPlatform,
        sleeperPlatform,
        yahooPlatform
    )

    val fantasyLeaguePlayers = fantasyPlatforms
        .map { platform -> platform.getLeaguePlayers() }
        .flatten()

    println("\nFantasy League Rosters:\n")

    fantasyLeaguePlayers.forEach { leaguePlayers ->
        println("----------")
        println("League: ${leaguePlayers.league.leagueName}")
        println("----------")
        leaguePlayers.players.forEach {
            if (it.status == null || it.status == "Active")
                println(it.fullName)
            else
                println("${it.fullName} : ${it.status}")
        }
        println()
    }
}
