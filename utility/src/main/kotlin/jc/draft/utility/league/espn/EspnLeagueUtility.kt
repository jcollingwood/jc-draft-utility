package jc.draft.utility.league.espn

import jc.draft.utility.league.Position
import jc.draft.utility.league.Status

fun getEspnPosition(position: Integer): Position {
    return when (position.toInt()) {
        1 -> Position.QB
        2 -> Position.RB
        3 -> Position.WR
        4 -> Position.TE
        5 -> Position.K
        16 -> Position.DST
        else -> Position.Unknown
    }
}

fun getEspnStatus(status: String?): Status {
    return when (status) {
        null,
        "ACTIVE" -> Status.Active

        "QUESTIONABLE" -> Status.Questionable
        "OUT" -> Status.Out
        "PUP" -> Status.PUP
        "IR" -> Status.IR
        else -> Status.Unknown
    }
}

fun isStartingLineupSlotId(lineupSlotId: Integer): Boolean {
    return !listOf(20, 21).contains(lineupSlotId.toInt())
}