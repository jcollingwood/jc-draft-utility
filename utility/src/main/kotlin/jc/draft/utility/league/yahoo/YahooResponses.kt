package jc.draft.utility.league.yahoo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class YahooFantasyContent(val team: YahooTeam)

@Serializable
data class YahooTeam(val roster: YahooRoster)

@Serializable
data class YahooRoster(val players: List<YahooPlayerWrapper>)

@Serializable
data class YahooPlayerWrapper(val player: YahooPlayer)

@Serializable
data class YahooPlayer(
    @SerialName("player_key") val playerKey: String? = null,
    val name: YahooPlayerName? = null,
    @SerialName("primary_position") val primaryPosition: String? = null
)

@Serializable
data class YahooPlayerName(val full: String)