package jc.draft.utility.api.service.stats

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class PfrPlayerGameStatsService {
    fun getPlayerStats(pfrId: String, year: Int = 2022): List<PfrGameStats> {
        return PfrPlayerDocument(pfrId, year).getActiveGameStats()
    }
}

data class PfrGameStats(
    val year: Int,
    val week: Int,
    val team: String,
    val opponent: String,
    // pass stats
    val passCompletions: Int,
    val passAttempts: Int,
    val passYards: Int,
    val passTds: Int,
    val interceptions: Int,
    val sacks: Int,
    val sackYards: Int,
    // rush stats
    val rushAttempts: Int,
    val rushYards: Int,
    val rushTds: Int,
    // pass stats
    val targets: Int,
    val receptions: Int,
    val recYards: Int,
    val recTds: Int
)

class PfrPlayerDocument(pfrId: String, private val year: Int) {
    companion object {
        private val PFR_GAME_LOG_URL =
            { pfrId: String, year: Int -> "https://www.pro-football-reference.com/players/$pfrId/gamelog/$year/" }
    }

    private val doc: Document = Jsoup.connect(PFR_GAME_LOG_URL(pfrId, year)).get()

    fun getActiveGameStats(): List<PfrGameStats> {
        return this.getActiveGameLogElements().map { row -> populateWeekStats(row, this.year) }
    }

    private fun getActiveGameLogElements(): List<Element> {
        return this.doc.select("table#stats > tbody > tr:not(.gamelog_dnp)")
    }

    private fun populateWeekStats(el: Element, year: Int): PfrGameStats {
        return PfrGameStats(
            year = year,
            week = getIntData(el, "week_num"),
            team = getStringData(el, "team"),
            opponent = getStringData(el, "opp"),
            // pass stats
            passCompletions = getIntData(el, "pass_cmp"),
            passAttempts = getIntData(el, "pass_att"),
            passYards = getIntData(el, "pass_yds"),
            passTds = getIntData(el, "pass_td"),
            interceptions = getIntData(el, "pass_int"),
            sacks = getIntData(el, "pass_sacked"),
            sackYards = getIntData(el, "pass_sacked_yds"),
            // rush stats
            rushAttempts = getIntData(el, "rush_att"),
            rushYards = getIntData(el, "rush_yds"),
            rushTds = getIntData(el, "rush_td"),
            // receiving stats
            targets = getIntData(el, "targets"),
            receptions = getIntData(el, "rec"),
            recYards = getIntData(el, "rec_yds"),
            recTds = getIntData(el, "rec_td")
        )
    }

    private fun getStringData(el: Element, statName: String): String {
        return getDataStat(el, statName) ?: "not_found"
    }
    
    private fun getIntData(el: Element, statName: String): Int {
        return getDataStat(el, statName)?.toInt() ?: 0
    }

    private fun getDataStat(el: Element, statName: String): String? {
        return el.selectFirst("td[data-stat=$statName]")?.text()
    }
}
