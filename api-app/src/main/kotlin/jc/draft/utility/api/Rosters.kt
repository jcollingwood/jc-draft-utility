package jc.draft.utility.api

import jc.draft.utility.FantasyLeagueService
import jc.draft.utility.league.FantasyPlayer
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.LeaguePlatform
import jc.draft.utility.league.Status
import jc.draft.utility.league.fantasyPlatformFactory
import jc.draft.utility.league.sleeper.SleeperFantasyPlatform
import kotlinx.html.FlowContent
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.ul

fun FlowContent.rostersBody(leagueService: FantasyLeagueService): Unit {
    span {

        h1 {
            classes = setOf("font-medium", "text-lg", "mb-4")
            +"Fantasy Rosters"
        }
    }
    val leagueNames = leagueService.getLeagues().map { it.leagueName }
    println(leagueNames)
    div {
        classes = setOf("grid", "grid-cols-1", "sm:grid-cols-2", "md:grid-cols-3", "gap-4")
        leagueNames.map {
            section {
                id = "league-$it"
                classes = setOf("flex", "flex-col")
                hxTrigger("load")
                hxGet("/rosters/leagues/$it")
                hxSwap("innerHTML")

                leagueSectionLoading(it)
            }
        }
    }
}

fun FlowContent.leagueSection(
    leagueService: FantasyLeagueService,
    leagueName: String,
    refetchPlayers: Boolean,
    fetchNew: Boolean
) {
    val leagueConfig = leagueService.getLeagues().find { leagueName == it.leagueName }

    leagueConfig?.let { league ->

        val fantasyPlatform = fantasyPlatformFactory(league.leaguePlatform)
        // refetch sleeper player data if triggered
        if (leagueConfig.leaguePlatform == LeaguePlatform.SLEEPER && refetchPlayers) {
            (fantasyPlatform as SleeperFantasyPlatform).refetchSleeperPlayers()
        }

        val leaguePlayers =
            fantasyPlatform.getLeaguePlayers(leagueConfig = league, fetchNew = fetchNew)

        div {
            classes = setOf("mb-4", "flex", "flex-col", "gap-2")
            leagueSectionHeader(league)
            leagueHeaderButtons(league)
        }
        ul {
            classes = setOf("gap-2")
            leaguePlayers.players.map { player ->
                leaguePlayer(player)
            }
        }
    } ?: p("Invalid league name: $leagueName")
}

fun FlowContent.leagueSectionHeader(league: LeagueConfig) {
    div {
        classes = setOf("flex", "flex-col")
        h2 {
            classes = setOf("text-lg", "font-medium")
            +league.leagueName
        }
        span {
            classes = setOf("text-sm", "text-gray-500", "flex", "flex-row", "gap-3")
            p { +league.leaguePlatform.displayValue }
            // other league details could go here
        }
    }
}

fun FlowContent.leagueHeaderButtons(league: LeagueConfig) {
    val leagueName = league.leagueName
    div {
        classes = setOf("flex", "gap-3")
        var leagueButtonClasses = setOf(
            "rounded-full",
            "p-1",
            "outline",
            "outline-1",
            "material-symbols-outlined"
        )
        // open league page in new tab
        a {
            classes = leagueButtonClasses + setOf("outline-blue-300", "text-blue-500", "hover:bg-blue-100")
            href = league.leagueUrl
            target = "_blank"
            +"open_in_new"
        }
        // primary button to refetch league player data
        button {
            hxTrigger("click")
            hxGet("/rosters/leagues/$leagueName?fetchNew=true")
            hxTarget("#league-$leagueName")

            classes = leagueButtonClasses + setOf(
                "outline-green-300",
                "text-green-500",
                "hover:bg-green-100",
            )
            +"refresh"
        }
        // sleeper only - trigger refetch of all sleeper player data
        if (league.leaguePlatform == LeaguePlatform.SLEEPER) {
            button {
                hxTrigger("click")
                hxGet("/rosters/leagues/$leagueName?refetchPlayers=true&fetchNew=true")
                hxTarget("#league-$leagueName")

                classes = leagueButtonClasses + setOf(
                    "outline-yellow-300",
                    "text-yellow-500",
                    "hover:bg-yellow-100",
                )
                +"system_update_alt"
            }
        }
    }
}

fun UL.leaguePlayer(player: FantasyPlayer) {
    li {
        val startingInd = if (player.isStarting) "border-green-300" else "border-gray-200"
        classes = setOf(
            "flex", "flex-row", "gap-3", "pl-3", "items-center", "border-l-4", startingInd
        )
        span {
            classes = setOf("text-sm", "font-bold")
            +player.position.name
        }
        span { +player.fullName }
        val status = player.status
        if (status != Status.Active)
            span {
                val color = if (status == Status.Questionable) "text-orange-400" else "text-red-400"
                classes = setOf(color, "text-sm", "italic")
                +status.displayValue
            }
    }
}

fun FlowContent.leagueSectionLoading(leagueName: String) {
    div {
        classes = setOf("mb-4", "flex", "flex-col", "gap-2")
        h2 {
            classes = setOf(
                "text-lg", "font-medium"
            )
            +leagueName
        }
        div {
            classes = setOf("flex", "gap-2")
            +"Loading..."
        }
    }
}
