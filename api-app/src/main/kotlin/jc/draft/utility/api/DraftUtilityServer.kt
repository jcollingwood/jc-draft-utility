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
                            link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
                            link(rel = "preconnect", href = "https://fonts.googleapis.com")
                            link(rel = "preconnect", href = "https://fonts.gstatic.com")
                            link(
                                href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500&display=swap",
                                rel = "stylesheet"
                            )
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
                    call.respondHtml {
                        val leagueName = call.parameters["leagueName"]
                        body {
                            leagueName.also {
                                leagueSection(it.toString())
                            } ?: p("Invalid league name")
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}
