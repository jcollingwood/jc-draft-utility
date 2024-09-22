package jc.draft.utility.api

import jc.draft.utility.league.fantasyLeagues
import jc.draft.utility.league.fantasyPlatformFactory
import kotlinx.html.FlowContent
import kotlinx.html.classes
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.ul

fun FlowContent.rostersBody(): Unit {
    h1 {
        +"fantasy rosters"
    }
    val leagueNames = fantasyLeagues.map { it.leagueName }
    leagueNames.map {
        section {
            hxTrigger("load")
            hxGet("/rosters/leagues/$it")
            hxSwap("outerHTML")
        }
    }
}

fun FlowContent.leagueSection(leagueName: String) {
    val leagueConfig = fantasyLeagues.find { leagueName == it.leagueName }
    leagueConfig?.let { league ->
        val leaguePlayers = fantasyPlatformFactory(league.leaguePlatform).getLeaguePlayers(league)

        h2 { league.leagueName }
        ul {

            leaguePlayers.players.map { player ->
                val status = player.status ?: "Active"
                println(player)
                if (status == "Active")
                    li { player.fullName }
                else
                    li {
                        p { "${player.fullName} " }
                        p {
                            classes = setOf("bad")
                            +status
                        }
                    }
            }
        }
    } ?: p("Invalid league name: $leagueName")
}
