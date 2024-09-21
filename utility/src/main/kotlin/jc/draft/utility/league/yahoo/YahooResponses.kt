package jc.draft.utility.league.yahoo

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import kotlinx.serialization.Serializable


@Serializable
data class YahooFantasyContent(val team: YahooTeam)

@Serializable
data class YahooTeam(val roster: YahooRoster)

@Serializable
data class YahooRoster(
    @JacksonXmlElementWrapper val players: List<YahooPlayer>
)

@Serializable
data class YahooPlayer(
    @JacksonXmlProperty(localName = "player_key") val playerKey: String? = null,
    val name: YahooPlayerName,
    @JacksonXmlProperty(localName = "primary_position") val primaryPosition: String? = null,
    val status: String? = null
)

@Serializable
data class YahooPlayerName(val full: String)