package jc.draft.utility.api

import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.netty.Netty
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.title


fun main() {
    embeddedServer(Netty, 8080) {
        routing {
            stylesCss()
            route("/rosters") {
                get {
                    call.respondHtml {
                        head {
                            title { +"fantasy rosters" }
                            link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                            script { src = "https://unpkg.com/htmx.org@2.0.2" }
                        }
                        rostersBody()
                    }
                }
                get("/platform/{nam}") {
                    call.respondHtml {
                        println(call.parameters["name"])
                        body {
                            platformBody(call.parameters["name"] ?: "test")
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}
