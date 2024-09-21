package jc.draft.utility.api

import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1

fun HTML.rostersBody(): Unit {
    body {
        h1 {
            classes = setOf("title")
            +"fantasy rosters"
        }
        div {
            hxTrigger("load delay:1s")
            hxGet("/rosters/platform/espn")
        }
        div {
            hxTrigger("load delay:2s")
            hxGet("/rosters/platform/sleeper")
        }
        div {
            hxTrigger("load delay:3s")
            hxGet("/rosters/platform/yahoo")
        }
    }
}

fun FlowContent.platformBody(platformName: String): Unit {
    div {
        h1 {
            +"platform: $platformName"
        }
    }
}
