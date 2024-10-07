package jc.draft.utility.league

fun main() {

    val fantasyLeaguePlayers = fantasyLeagues
        .map { league -> fantasyPlatformFactory(league.leaguePlatform).getLeaguePlayers(league) }

    println("\nFantasy League Rosters:\n")

    fantasyLeaguePlayers.forEach { leaguePlayers ->
        println("----------")
        println("League: ${leaguePlayers.league.leagueName}")
        println("----------")
        leaguePlayers.players.forEach {
            if (it.status == Status.Active)
                println(it.fullName)
            else
                println("${it.fullName} : ${it.status}")
        }
        println()
    }
}
