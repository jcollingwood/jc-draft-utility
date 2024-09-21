package jc.draft.utility.api

import kotlinx.html.HTMLTag

fun HTMLTag.hxPost(to: String) {
    this.attributes["hx-post"] = to
}

fun HTMLTag.hxGet(from: String) {
    this.attributes["hx-get"] = from
}

fun HTMLTag.hxSwap(with: String = "innerHTML") {
    this.attributes["hx-swap"] = with
}

fun HTMLTag.hxTrigger(by: String = "click") {
    this.attributes["hx-trigger"] = by
}

fun HTMLTag.hxTarget(target: String = "#content") {
    this.attributes["hx-target"] = target
}