package jc.draft.utility.app.service

import jc.draft.utility.app.domain.player.Player
import jc.draft.utility.app.domain.player.PositionEnum
import jc.draft.utility.app.domain.player.TeamEnum


class PlayerService {
    fun getPlayers(): List<Player> {
        return players
    }
}

val players = listOf(
    Player(1, "Jonathon Taylor", PositionEnum.RB, TeamEnum.IND, 14, 1, 1.0),
    Player(2, "Christian McCaffrey", PositionEnum.RB, TeamEnum.CAR, 13, 2, 2.0),
    Player(3, "Justin Jefferson", PositionEnum.WR, TeamEnum.MIN, 7, 3, 3.0),
    Player(4, "Austin Ekeler", PositionEnum.RB, TeamEnum.LAC, 8, 4, 4.0),
    Player(5, "Derrick Henry", PositionEnum.RB, TeamEnum.TEN, 6, 5, 5.0)
)