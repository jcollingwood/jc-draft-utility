package jc.draft.utility.api

import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import jc.draft.utility.FantasyLeagueConfigService
import jc.draft.utility.api.auth.UserSession
import jc.draft.utility.api.auth.authenticate
import jc.draft.utility.api.rosters.LeagueService
import jc.draft.utility.league.sleeper.SleeperPlayerService
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.head
import kotlinx.html.main
import kotlinx.html.p
import kotlinx.html.title
import java.io.File
import kotlin.text.toBoolean

fun Application.configureRouting(httpClient: HttpClient) {
    /* services init */
    val leagueConfigService = FantasyLeagueConfigService()
    val sleeperPlayerService = SleeperPlayerService(httpClient)
    val leagueService = LeagueService(
        httpClient = httpClient,
        sleeperPlayerService = sleeperPlayerService
    )

    routing {
        // static directory route relative to project root dir, should pull in tailwind css
        staticFiles("/static", File("api-app/src/main/resources/static"))

        route("/rosters") {
            get {
                val userSession: UserSession? = authenticate(call)
                if (userSession == null) return@get

                call.respondHtml {
                    head {
                        title { +"Fantasy Rosters" }
                        stylesAndFonts()
                        matIcons()
                        htmx()
                    }
                    body {
                        main {
                            classes =
                                setOf(
                                    "font-inter", "flex", "flex-col", "h-full", "w-screen", "items-center", "p-4"
                                )
                            rostersBody(
                                leagueConfigService = leagueConfigService,
                                sleeperPlayerService = sleeperPlayerService
                            )
                        }
                    }
                }
            }
            get("/leagues/{leagueName}") {
                val userSession: UserSession? = authenticate(call)
                if (userSession == null) return@get

                var refetchPlayers = call.request.queryParameters["refetchPlayers"]?.toBoolean() == true
                var fetchNew = call.request.queryParameters["fetchNew"]?.toBoolean() == true
                val leagueName = call.parameters["leagueName"]

                // missing league name
                if (leagueName == null) call.respondHtml(HttpStatusCode.BadRequest) { body { p("Invalid league name") } }

                if (refetchPlayers) sleeperPlayerService.getPlayers(true)

                call.respondHtml {
                    body {
                        leagueSection(
                            leagueConfigService = leagueConfigService,
                            leagueService = leagueService,
                            leagueName = leagueName.toString(),
                            fetchNew = fetchNew,
                        )
                    }
                }
            }
        }
    }
}

