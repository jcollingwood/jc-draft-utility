package jc.draft.utility.league.yahoo

import jc.draft.utility.league.Position
import jc.draft.utility.league.Status

fun getYahooPosition(position: String?): Position {
    return when (position) {
        "QB" -> Position.QB
        "RB" -> Position.RB
        "WR" -> Position.WR
        "TE" -> Position.TE
        "K" -> Position.K
        "DEF" -> Position.DST
        else -> Position.Unknown
    }
}

fun getYahooStatus(status: String?): Status {
    return when (status) {
        null -> Status.Active
        "Q" -> Status.Questionable
        "D" -> Status.Doubtful
        "O" -> Status.Out
        "PUP-R" -> Status.PUP
        "IR" -> Status.IR
        else -> {
            println("unknown yahoo status: $status")
            Status.Unknown
        }
    }
}