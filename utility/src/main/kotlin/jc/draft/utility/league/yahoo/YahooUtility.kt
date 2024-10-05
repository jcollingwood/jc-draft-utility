package jc.draft.utility.league.yahoo

import jc.draft.utility.league.Position

fun getYahooPosition(position: String?): Position {
    return when (position) {
        "QB" -> Position.QB
        "RB" -> Position.RB
        "WR" -> Position.WR
        "TE" -> Position.TE
        "K" -> Position.K
        "DEF" -> Position.DST
        else -> Position.UNKNOWN
    }
}