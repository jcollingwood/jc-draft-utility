package jc.draft.utility.league

import jc.draft.utility.league.espn.EspnFantasyPlatform
import jc.draft.utility.league.sleeper.SleeperFantasyPlatform

fun main() {
    // TODO sleeper player data all show as Active status
    val fantasyLeagues = listOf(EspnFantasyPlatform(), SleeperFantasyPlatform())
        .map { platform -> platform.getPlayers() }
        .flatten()

    println("\nFantasy League Rosters:\n")

    fantasyLeagues.forEach { leaguePlayers ->
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