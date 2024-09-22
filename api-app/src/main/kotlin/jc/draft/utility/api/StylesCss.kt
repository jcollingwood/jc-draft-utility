package jc.draft.utility.api

import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.css.Color
import kotlinx.css.CssBuilder
import kotlinx.css.Margin
import kotlinx.css.body
import kotlinx.css.color
import kotlinx.css.margin
import kotlinx.css.px


fun Route.stylesCss() {
    get("/styles.css") {
        call.respondCss {
            body {
                margin = Margin(20.px, 50.px)
            }
            rule(".bad") {
                color = Color.darkRed
            }
        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
