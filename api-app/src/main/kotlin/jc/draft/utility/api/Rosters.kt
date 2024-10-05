package jc.draft.utility.api

import jc.draft.utility.league.fantasyLeagues
import jc.draft.utility.league.fantasyPlatformFactory
import kotlinx.html.FlowContent
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.ul

fun FlowContent.rostersBody(): Unit {
    h1 {
        classes = setOf("font-medium", "text-lg", "mb-4")
        +"Fantasy Rosters"
    }
    val leagueNames = fantasyLeagues.map { it.leagueName }
    div {
        classes = setOf("grid", "grid-cols-3", "gap-4")
        leagueNames.map {
            section {
                classes = setOf("flex", "flex-col")
                hxTrigger("load")
                hxGet("/rosters/leagues/$it")
                hxSwap("innerHTML")
            }
        }
    }
}

fun FlowContent.leagueSection(leagueName: String) {
    val leagueConfig = fantasyLeagues.find { leagueName == it.leagueName }
    leagueConfig?.let { league ->
        val leaguePlayers = fantasyPlatformFactory(league.leaguePlatform).getLeaguePlayers(league)

        h2 {
            classes = setOf("font-medium", "mb-4")
            +league.leagueName
        }
        ul {
            leaguePlayers.players.map { player ->
                val status = player.status ?: "Active"
                if (status == "Active")
                    li { p { +"${if (player.isStarting) "*" else ""}${player.position} ${player.fullName}" } }
                else
                    li {
                        div {
                            classes = setOf("flex", "flex-row", "gap-2")
                            span { +"${if (player.isStarting) "*" else ""}${player.position} ${player.fullName}" }
                            span {
                                // TODO standardize statuses, atm workaround to gather all questionable just checks first letter
                                val color = if (status.first() == 'Q') "text-orange-400" else "text-red-400"
                                classes = setOf(color)

                                +status
                            }
                        }
                    }
            }
        }
    } ?: p("Invalid league name: $leagueName")
}
