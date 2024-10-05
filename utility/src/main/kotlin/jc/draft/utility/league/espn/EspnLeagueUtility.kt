package jc.draft.utility.league.espn

import jc.draft.utility.league.Position

fun getEspnPosition(position: Integer): Position {
    return when (position.toInt()) {
        1 -> Position.QB
        2 -> Position.RB
        3 -> Position.WR
        4 -> Position.TE
        5 -> Position.K
        16 -> Position.DST
        else -> Position.UNKNOWN
    }
}