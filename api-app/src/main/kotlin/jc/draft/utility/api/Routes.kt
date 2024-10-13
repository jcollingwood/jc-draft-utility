package jc.draft.utility.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import jc.draft.utility.FantasyLeagueService
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.head
import kotlinx.html.main
import kotlinx.html.p
import kotlinx.html.title
import java.io.File
import kotlin.text.toBoolean

fun Application.configureRouting(leagueService: FantasyLeagueService = FantasyLeagueService()) {
    routing {
        // static directory route relative to project root dir, should pull in tailwind css
        staticFiles("/static", File("api-app/src/main/resources/static"))

        route("/rosters") {
            get {
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
                            rostersBody(leagueService)
                        }
                    }
                }
            }
            get("/leagues/{leagueName}") {
                var refetchPlayers = call.request.queryParameters["refetchPlayers"]?.toBoolean() == true
                var fetchNew = call.request.queryParameters["fetchNew"]?.toBoolean() == true
                val leagueName = call.parameters["leagueName"]

                // missing league name
                if (leagueName == null) call.respondHtml(HttpStatusCode.BadRequest) { body { p("Invalid league name") } }

                call.respondHtml {
                    body {
                        leagueSection(
                            leagueService = leagueService,
                            leagueName = leagueName.toString(),
                            fetchNew = fetchNew,
                            refetchPlayers = refetchPlayers
                        )
                    }
                }
            }
        }
    }
}