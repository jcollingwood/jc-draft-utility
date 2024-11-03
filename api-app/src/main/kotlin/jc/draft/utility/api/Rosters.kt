package jc.draft.utility.api

import jc.draft.utility.FantasyLeagueConfigService
import jc.draft.utility.api.rosters.LeagueService
import jc.draft.utility.league.FantasyPlayer
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.LeaguePlatform
import jc.draft.utility.league.Status
import jc.draft.utility.league.yahoo.YAHOO_AUTH_CONFIG
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

fun FlowContent.rostersBody(
    leagueConfigService: FantasyLeagueConfigService,
): Unit {
    val leagues = leagueConfigService.getLeagues()

    span {
        h1 {
            classes = setOf("font-medium", "text-lg", "mb-4")
            +"Fantasy Rosters"
        }
    }
    div {
        classes = setOf("grid", "grid-cols-1", "sm:grid-cols-2", "md:grid-cols-3", "gap-4")
        leagues.map {
            section {
                id = "league-${it.id}"
                classes = setOf("flex", "flex-col")
                hxTrigger("load")
                hxGet("/rosters/leagues/${it.id}")
                hxSwap("innerHTML")

                leagueSectionLoading(it.leagueName)
            }
        }
    }
}

fun FlowContent.leagueSection(
    leagueConfigService: FantasyLeagueConfigService,
    leagueService: LeagueService,
    leagueId: Int,
    fetchNew: Boolean
) {
    val leagueConfig = leagueConfigService.getLeagueById(leagueId)

    if (leagueConfig == null) return p("League not found with id: $leagueId")

    val leaguePlayers = leagueService.getLeagueRoster(
        leagueConfig = leagueConfig,
        fetchNew = fetchNew
    )

    div {
        classes = setOf("mb-4", "flex", "flex-col", "gap-2")
        leagueSectionHeader(leagueConfig)
        leagueHeaderButtons(leagueConfig)
    }
    if (leaguePlayers.players.isNotEmpty()) {
        ul {
            classes = setOf("gap-2")
            leaguePlayers.players.map { player ->
                leaguePlayer(player)
            }
        }
    } else {
        configureLeague(leagueConfig)
    }
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
            hxGet("/rosters/leagues/${league.id}?fetchNew=true")
            hxTarget("#league-${league.id}")

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
                hxGet("/rosters/leagues/${league.id}?refetchPlayers=true&fetchNew=true")
                hxTarget("#league-${league.id}")

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

fun FlowContent.configureLeague(league: LeagueConfig) {
    when (league.leaguePlatform) {
        LeaguePlatform.YAHOO -> {
            p("Probably need a new yahoo auth code")
            a {
                href =
                    "https://api.login.yahoo.com/oauth2/request_auth?client_id=${YAHOO_AUTH_CONFIG.yahooClientId}&response_type=code&redirect_uri=${YAHOO_AUTH_CONFIG.yahooRedirectUri}"
                target = "_blank"
                +"get new yahoo auth code"
                p("open_in_new")
            }
        }

        LeaguePlatform.ESPN -> {
            p("ESPN cookies not configured probably")
        }

        LeaguePlatform.SLEEPER -> {
            p("The developer probably messed this one up")
        }
    }
}
