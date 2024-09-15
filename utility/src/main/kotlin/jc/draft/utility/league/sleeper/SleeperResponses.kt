package jc.draft.utility.league.sleeper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SleeperLeagueRoster(
    @SerialName("owner_id") val ownerId: String,
    val players: List<String>,
    val starters: List<String>
)