package jc.draft.utility.api.service

import jc.draft.utility.api.domain.player.Player
import jc.draft.utility.api.domain.player.PositionEnum
import jc.draft.utility.api.domain.player.TeamEnum

data class PlayerSearchCriteria(
    val name: String? = null,
    val positionEnum: PositionEnum? = null,
    val teamEnum: TeamEnum? = null
)

class PlayerService {
    fun getPlayers(search: PlayerSearchCriteria? = PlayerSearchCriteria()): List<Player> {
        return players.filter { playerFilterFunc(it, search) }
    }

    fun getPlayerById(id: Int): Player {
        return getPlayerHistoryById(id).first()
    }

    fun getPlayerHistoryById(id: Int): List<Player> {
        return players.filter { id == it.id }.sortedBy { it.version }.reversed()
    }

    fun createPlayer(player: Player): Player {
        return savePlayer(player)
    }

    fun updatePlayer(id: Int, player: Player): Player {
        val existing = getPlayerById(id)
        println("updating player ${existing.name}\nold:$existing\nnew:$player")
        val newPlayer = Player(
            player.id,
            player.name,
            player.position,
            player.team,
            player.bye,
            player.rank,
            player.adp,
            existing.version + 1
        )
        return savePlayer(newPlayer)
    }

    private fun savePlayer(player: Player): Player {
        players.add(player)
        return player
    }

    private fun playerFilterFunc(player: Player, search: PlayerSearchCriteria?): Boolean {
        return search?.let { s ->
            s.name?.let { player.name.contains(it, true) } ?: true
                    && s.positionEnum?.let { it == player.position } ?: true
                    && s.teamEnum?.let { it == player.team } ?: true
        } ?: true
    }
}

val players = mutableListOf(
    Player(1, "Jonathon Taylor", PositionEnum.RB, TeamEnum.IND, 14, 1, 1.0),
    Player(1, "Jonathon Taylor", PositionEnum.RB, TeamEnum.IND, 14, 1, 1.5, 2),
    Player(2, "Christian McCaffrey", PositionEnum.RB, TeamEnum.CAR, 13, 2, 2.0),
    Player(2, "Christian McCaffrey", PositionEnum.RB, TeamEnum.CAR, 13, 2, 1.5, 2),
    Player(3, "Justin Jefferson", PositionEnum.WR, TeamEnum.MIN, 7, 3, 3.0),
    Player(4, "Austin Ekeler", PositionEnum.RB, TeamEnum.LAC, 8, 4, 4.0),
    Player(5, "Derrick Henry", PositionEnum.RB, TeamEnum.TEN, 6, 5, 5.0)
)