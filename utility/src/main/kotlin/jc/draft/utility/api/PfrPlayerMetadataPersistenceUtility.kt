package jc.draft.utility.api

import jc.draft.utility.api.service.stats.PfrPlayerJsoupMetadataService
import jc.draft.utility.api.service.stats.PfrPlayerMetadata
import jc.draft.utility.data.databaseConnect
import jc.draft.utility.data.entities.PlayerEntity
import jc.draft.utility.data.entities.Players
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    addOrUpdateExistingRecords(getPfrPlayerMetadata())
}

fun getPfrPlayerMetadata(): List<PfrPlayerMetadata> {
    val alphabet: List<Char> = ('A'..'Z').toMutableList()
    println(alphabet)
    println("retrieving records for players named [$alphabet]")
    val totalMetadata = alphabet.map { letter ->
//        println("letter[$letter]: retrieving player metadata")
        val metadata = PfrPlayerJsoupMetadataService().getPlayerMetadata(letter)
//        println("letter[$letter]: ${metadata.size} records retrieved")
        return@map metadata
    }.flatten()
    println("total pfrPlayerMetadata records retrieved: ${totalMetadata.size}")
    return totalMetadata
}

fun addOrUpdateExistingRecords(players: List<PfrPlayerMetadata>) {
    databaseConnect()
    transaction {

        // addLogger(StdOutSqlLogger) // uncomment to show output sql
        players.forEach { p ->
            try {
                PlayerEntity.find { Players.pfrId eq p.pfrId }
                    .limit(1)
                    .single()
                    .let { ent ->
                        if (ent.name != p.name) {
                            println("player[${ent.pfrId}]: updating player name from [${ent.name}] to [${p.name}]")
                            ent.name = p.name
                        }
                        if (ent.position != p.position) {
                            println("player[${ent.pfrId}]: updating player position from [${ent.name}] to [${p.name}]")
                            ent.name = p.name
                        }
                    }
            } catch (e: NoSuchElementException) {
                println("player[${p.pfrId}]: create player: $p")
                PlayerEntity.new {
                    name = p.name
                    position = p.position
                    pfrId = p.pfrId
                }
            }
        }
    }
}