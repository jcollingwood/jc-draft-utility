package jc.draft.utility.league.espn

import jc.draft.utility.league.Position
import jc.draft.utility.league.Status
import mu.two.KotlinLogging

val log = KotlinLogging.logger {}

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
        "DOUBTFUL" -> Status.Doubtful
        "INJURY_RESERVE",
        "IR" -> Status.IR

        else -> {
            log.warn("unknown espn status: $status")
            Status.Unknown
        }
    }
}

/**
 * 20 = bench?
 * 21 = IR?
 */
fun isStartingLineupSlotId(lineupSlotId: Integer): Boolean {
    return !listOf(20, 21).contains(lineupSlotId.toInt())
}