package jc.draft.utility.league.sleeper

import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import jc.draft.utility.league.CacheableData
import jc.draft.utility.league.client
import jc.draft.utility.league.jsonParser
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class SleeperPlayer(
    val status: String? = null,
    val active: Boolean? = null,
//    val fantasy_positions: List<String> = emptyList(),
    val position: String? = null,
    val team: String? = null,
    val last_name: String? = null,
    val first_name: String? = null,
    val espn_id: Int? = null,
    val yahoo_id: Int? = null
)

data class SleeperConfig(val id: String?)

fun getSleeperPlayers(): Map<String, SleeperPlayer>? {
    return jsonParser.decodeFromString<Map<String, SleeperPlayer>>(SleeperPlayersData().getData(SleeperConfig("1")))
}

class SleeperPlayersData : CacheableData<SleeperConfig> {
    override fun directory(c: SleeperConfig): String {
        return "sleeper/players"
    }

    override fun refreshData(c: SleeperConfig): String {
        return runBlocking {
            val response: HttpResponse = client.request("https://api.sleeper.app/v1/players/nfl") {
                method = HttpMethod.Get
            }

            when (response.status) {
                HttpStatusCode.OK -> println("successfully retrieved sleeper player data")
                else -> println("failed to retrieve sleeper player data : \nstatus:${response.status}\nbody:${response.bodyAsText()}")
            }
            return@runBlocking response.bodyAsText()
        }
    }
}