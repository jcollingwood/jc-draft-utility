package jc.draft.utility

import jc.draft.utility.data.entities.FantasyLeagueEntity
import jc.draft.utility.league.LeagueConfig
import jc.draft.utility.league.LeaguePlatform
import org.jetbrains.exposed.sql.transactions.transaction

class FantasyLeagueConfigService {
    fun getLeagues(): List<LeagueConfig> {
        return transaction {
            return@transaction FantasyLeagueEntity.all().map {
                mapEntityToLeagueConfig(it)
            }
        }
    }

    fun getLeagueById(id: Int): LeagueConfig? {
        return transaction {
            return@transaction FantasyLeagueEntity.findById(id)?.let {
                mapEntityToLeagueConfig(it)
            }
        }
    }

    private fun mapEntityToLeagueConfig(entity: FantasyLeagueEntity): LeagueConfig {
        return LeagueConfig(
            id = entity.id.value,
            leaguePlatform = LeaguePlatform.valueOf(entity.leaguePlatform),
            year = entity.year,
            leagueName = entity.leagueName,
            leagueId = entity.leagueId,
            teamId = entity.teamId
        )
    }
}