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
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.title
import java.io.File


fun main() {
    embeddedServer(Netty, 8081) {
        routing {
            // static directory route relative to project root dir
            staticFiles("/static", File("api-app/src/main/resources/static"))

            route("/rosters") {
                get {
                    call.respondHtml {
                        head {
                            title { +"fantasy rosters" }
                            // tailwind css stylesheet
                            link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
                            // google fonts + Inter font
                            link(rel = "preconnect", href = "https://fonts.googleapis.com")
                            link(rel = "preconnect", href = "https://fonts.gstatic.com")
                            link(
                                href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500&display=swap",
                                rel = "stylesheet"
                            )
                            // refresh mat icon
                            link(
                                rel = "stylesheet",
                                href = "https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20,400,0,0"
                            )
                            // system alt mat icon
                            link(
                                rel = "stylesheet",
                                href = "https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20,400,0,0"
                            )
                            // htmx dep
                            script { src = "https://unpkg.com/htmx.org@2.0.2" }
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
