package jc.draft.utility.api

import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.Netty
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.head
import kotlinx.html.main
import kotlinx.html.p
import kotlinx.html.title
import java.io.File


fun main() {
    embeddedServer(Netty, 8081) {
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
                                        "font-inter",
                                        "flex",
                                        "flex-col",
                                        "h-screen",
                                        "w-screen",
                                        "items-center",
                                        "p-4"
                                    )
                                rostersBody()
                            }
                        }
                    }
                }
                get("/leagues/{leagueName}") {
                    var refetchPlayers = call.request.queryParameters["refetchPlayers"]?.toBoolean() == true
                    var fetchNew = call.request.queryParameters["fetchNew"]?.toBoolean() == true
                    call.respondHtml {
                        val leagueName = call.parameters["leagueName"]
                        body {
                            leagueName.also {
                                leagueSection(
                                    leagueName = it.toString(),
                                    fetchNew = fetchNew,
                                    refetchPlayers = refetchPlayers
                                )
                            } ?: p("Invalid league name")
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}
