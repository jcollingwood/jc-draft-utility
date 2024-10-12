package jc.draft.utility.data.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object FantasyLeague : IntIdTable("fantasy_league") {
    val leaguePlatform = varchar("league_platform", 255)
    val year = integer("year")
    val leagueName = varchar("league_name", 255)
    val leagueId = varchar("league_id", 255)
    val teamId = varchar("team_id", 255)
}

class FantasyLeagueEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FantasyLeagueEntity>(FantasyLeague)

    var leaguePlatform by FantasyLeague.leaguePlatform
    var year by FantasyLeague.year
    var leagueName by FantasyLeague.leagueName
    var leagueId by FantasyLeague.leagueId
    var teamId by FantasyLeague.teamId
}