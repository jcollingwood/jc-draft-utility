package jc.draft.utility

import jc.draft.utility.league.yahoo.YahooAuthService

fun main() {
    val yahooAuth = YahooAuthService().getYahooAccessToken()
    println(yahooAuth)
}