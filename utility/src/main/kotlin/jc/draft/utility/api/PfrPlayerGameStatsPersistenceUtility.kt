package jc.draft.utility.api

import jc.draft.utility.api.service.stats.PfrPlayerGameStatsService
import jc.draft.utility.data.databaseConnect
import jc.draft.utility.data.entities.PlayerEntity
import jc.draft.utility.data.entities.PlayerGameStats
import jc.draft.utility.data.entities.PlayerGameStatsEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
    val year = if (args.isEmpty()) 2022 else args[0].toInt()
    addOrUpdateExistingGameStatsRecordsForAllPlayers(year)
}

fun addOrUpdateExistingGameStatsRecordsForAllPlayers(year: Int) {
    databaseConnect()
    transaction {
        PlayerEntity.all().forEach { plyrEnt ->
            println("retrieving stats for player ${plyrEnt.name} (${plyrEnt.pfrId}) for $year")

            try {
                PlayerGameStatsEntity.find { PlayerGameStats.playerId eq plyrEnt.id }
                    .limit(1).single().let {
                        println("skipping any updates for $it")
                    }
            } catch (e: NoSuchElementException) {
                persistGameStatsRecordsForPlayer(year, plyrEnt.pfrId, plyrEnt.id)

                // waiting 5 seconds between requests to circumvent 429 rate limiting
                Thread.sleep(5000)
            }
        }
    }
}

fun persistGameStatsRecordsForPlayer(year: Int, pfrId: String, plyrId: EntityID<Int>) {
    transaction {
        PfrPlayerGameStatsService().getPlayerStats(pfrId, year).forEach { g ->
            PlayerGameStatsEntity.new {
                playerId = plyrId
                this.year = g.year
                week = g.week
                team = g.team
                opponent = g.opponent

                passComps = g.passCompletions
                passAtts = g.passAttempts
                passYds = g.passYards
                passTds = g.passTds
                ints = g.interceptions
                sacks = g.sacks

                rushAtts = g.rushAttempts
                rushYds = g.rushYards
                rushTds = g.rushTds

                tgts = g.targets
                recs = g.receptions
                recYds = g.recYards
                recTds = g.recTds
            }
        }
    }
    println("completed inserting game stats for $pfrId for $year")
}