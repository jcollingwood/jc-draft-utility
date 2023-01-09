package jc.draft.utility.api.domain.player

enum class PositionEnum {
    QB, RB, WR, TE
}

data class Player(
    val id: Int,
    val name: String,
    val position: PositionEnum,
    val team: TeamEnum,
    val bye: Int,
    val rank: Int,
    val adp: Double,
    val version: Int = 1
)