package jc.draft.utility.api

import jc.draft.utility.api.service.stats.PfrPlayerGameStatsService
import jc.draft.utility.api.service.stats.PfrPlayerJsoupMetadataService

val ZEKE = "E/ElliEz00"
val JT = "T/TaylJo02"
val CMC = "M/McCaCh01"
val KYLE = "A/AlleKy00"
val PFR_IDS = listOf(KYLE)

fun main() {
    printPlayerGameStats()
//    printPlayerMetadata()
}

fun printPlayerGameStats() {
    PFR_IDS.forEach { plyrId ->
        println("Retrieving week stats for $plyrId")
        PfrPlayerGameStatsService().getPlayerStats(plyrId).forEach { println(it) }
    }
}

fun printPlayerMetadata() {
    listOf('A').forEach { letter ->
        println("Retrieving player metadata for the letter $letter")
        PfrPlayerJsoupMetadataService().getPlayerMetadata(letter).forEach { println(it) }
    }
}
