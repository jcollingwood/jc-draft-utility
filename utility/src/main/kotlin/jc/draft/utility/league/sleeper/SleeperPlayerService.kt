package jc.draft.utility.league.sleeper

import io.ktor.client.HttpClient
import jc.draft.utility.league.jsonParser

class SleeperPlayerService(
    httpClient: HttpClient,
    private val sleeperPlayersData: SleeperPlayersData = SleeperPlayersData(httpClient)
) {
    fun getPlayers(fetchNew: Boolean = false): Map<String, SleeperPlayer>? {
        return jsonParser.decodeFromString<Map<String, SleeperPlayer>>(
            sleeperPlayersData.lockedGetData(
                c = SleeperConfig("1"),
                fetchNew = fetchNew
            )
        )
    }
}