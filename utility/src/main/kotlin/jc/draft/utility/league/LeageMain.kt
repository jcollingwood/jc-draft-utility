package jc.draft.utility.league

import mu.two.KotlinLogging

val log = KotlinLogging.logger {}

fun main() {

    val fantasyLeaguePlayers = fantasyLeagues
        .map { league -> fantasyPlatformFactory(league.leaguePlatform).getLeaguePlayers(league) }

    log.info("\nFantasy League Rosters:\n")

    fantasyLeaguePlayers.forEach { leaguePlayers ->
        log.info("----------")
        log.info("League: ${leaguePlayers.league.leagueName}")
        log.info("----------")
        leaguePlayers.players.forEach {
            if (it.status == Status.Active)
                log.info(it.fullName)
            else
                log.info("${it.fullName} : ${it.status}")
        }
        println()
    }
}
