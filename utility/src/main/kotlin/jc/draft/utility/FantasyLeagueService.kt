package jc.draft.utility

import jc.draft.utility.data.entities.FantasyLeagueEntity
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.LeaguePlatform
import org.jetbrains.exposed.sql.transactions.transaction

class FantasyLeagueService {
    fun getLeagues(): List<LeagueConfig> {
        return transaction {
            return@transaction listOf(FantasyLeagueEntity.all().map {
                LeagueConfig(
                    leaguePlatform = LeaguePlatform.valueOf(it.leaguePlatform),
                    year = it.year,
                    leagueName = it.leagueName,
                    leagueId = it.leagueId,
                    teamId = it.teamId
                )
            }[4])
        }
//        return fantasyLeagues
    }
}