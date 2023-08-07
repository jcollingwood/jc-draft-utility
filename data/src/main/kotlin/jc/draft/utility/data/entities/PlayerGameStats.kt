package jc.draft.utility.data.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object PlayerGameStats : IntIdTable() {
    val playerId = reference("playerid", Players)
    val year = integer("year")
    val week = integer("week")
    val team = varchar("team", 255)
    val opponent = varchar("opponent", 255)

    // pass stats
    val passComps = integer("passcomps")
    val passAtts = integer("passatts")
    val passYds = integer("passyds")
    val passTds = integer("passtds")
    val ints = integer("ints")
    val sacks = integer("sacks")

    // rush stats
    val rushAtts = integer("rushatts")
    val rushYds = integer("rushyds")
    val rushTds = integer("rushtds")

    // receiving stats
    val tgts = integer("tgts")
    val recs = integer("recs")
    val recYds = integer("recyds")
    val recTds = integer("rectds")
}

class PlayerGameStatsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerGameStatsEntity>(PlayerGameStats)

    var playerId by PlayerGameStats.playerId
    var year by PlayerGameStats.year
    var week by PlayerGameStats.week
    var team by PlayerGameStats.team
    var opponent by PlayerGameStats.opponent

    var passComps by PlayerGameStats.passComps
    var passAtts by PlayerGameStats.passAtts
    var passYds by PlayerGameStats.passYds
    var passTds by PlayerGameStats.passTds
    var ints by PlayerGameStats.ints
    var sacks by PlayerGameStats.sacks

    var rushAtts by PlayerGameStats.rushAtts
    var rushYds by PlayerGameStats.rushYds
    var rushTds by PlayerGameStats.rushTds

    var tgts by PlayerGameStats.tgts
    var recs by PlayerGameStats.recs
    var recYds by PlayerGameStats.recYds
    var recTds by PlayerGameStats.recTds
}