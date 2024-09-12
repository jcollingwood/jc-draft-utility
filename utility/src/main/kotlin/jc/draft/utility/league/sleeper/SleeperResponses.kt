package jc.draft.utility.league.sleeper

import kotlinx.serialization.Serializable

@Serializable
data class SleeperLeagueRoster(
    val owner_id: String,
    val players: List<String>,
    val starters: List<String>
)