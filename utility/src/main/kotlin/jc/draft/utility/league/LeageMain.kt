package jc.draft.utility.league

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import jc.draft.utility.league.sleeper.SleeperPlayerService
import mu.two.KotlinLogging

val log = KotlinLogging.logger {}

fun main() {
    val client = HttpClient(CIO)
    val sleeperPlayerService = SleeperPlayerService(client)
    val platformFactory = FantasyPlatformFactory(
        httpClient = client,
        sleeperPlayerService = sleeperPlayerService
    )

    val fantasyLeaguePlayers = fantasyLeagues
        .map { league -> platformFactory.getPlatform(league.leaguePlatform).getLeaguePlayers(league) }

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
