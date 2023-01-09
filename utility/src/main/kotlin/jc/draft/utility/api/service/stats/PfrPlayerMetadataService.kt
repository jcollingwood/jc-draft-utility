package jc.draft.utility.api.service.stats

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

data class PfrPlayerMetadata(
    val name: String,
    val position: String,
    val pfrId: String
)

class PfrPlayerListDocument(letter: Char) {
    companion object {
        private val PFR_PLAYERS_URL =
            { letter: Char -> "https://www.pro-football-reference.com/players/$letter/" }
        private const val PLAYER_PARSE_REGEX = "(.*) \\((.*)\\)"
        private const val PLAYER_ID_REGEX = "/players/(.*).htm"
    }

    private val doc: Document = Jsoup.connect(PFR_PLAYERS_URL(letter)).get()

    fun getActivePlayerMetadata(): List<PfrPlayerMetadata> {
        return this.getActivePlayers().map { parsePlayerMetadata(it) }
    }

    private fun getActivePlayers(): Elements {
        return doc.select("div#div_players > p > b")
    }

    private fun parsePlayerMetadata(el: Element): PfrPlayerMetadata {
        val playerParts = PLAYER_PARSE_REGEX.toRegex().find(el.text())
        return PfrPlayerMetadata(
            name = playerParts?.groupValues?.get(1) ?: "not_found",
            position = playerParts?.groupValues?.get(2) ?: "not_found",
            pfrId = PLAYER_ID_REGEX.toRegex().find(el.select("a").attr("href"))?.groupValues?.get(1) ?: "not_found"
        )
    }
}

class PfrPlayerJsoupMetadataService {
    fun getPlayerMetadata(
        letter: Char,
        positions: List<String> = listOf("QB", "RB", "WR", "TE")
    ): List<PfrPlayerMetadata> {
        return PfrPlayerListDocument(letter).getActivePlayerMetadata()
            .filter { positions.contains(it.position) }
    }
}