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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SleeperPlayer(
    val status: String? = null,
    val active: Boolean? = null,
//    val fantasy_positions: List<String> = emptyList(),
    val position: String? = null,
    val team: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("espn_id") val espnId: Int? = null,
    @SerialName("yahoo_id") val yahoo_id: Int? = null
)

data class SleeperConfig(val id: String?)

fun getSleeperPlayers(): Map<String, SleeperPlayer>? {
    return jsonParser.decodeFromString<Map<String, SleeperPlayer>>(SleeperPlayersData().getData(SleeperConfig("1")))
}

/**
 * Sleeper is public and readonly so no auth needed
 */
class SleeperPlayersData : CacheableData<SleeperConfig> {
    override fun directory(c: SleeperConfig): String {
        return "sleeper/players"
    }

    // this is a large set of data and should be retrieved at most once per day
    override fun refreshDurationHours(): Long {
        return 24
    }

    override fun refreshData(c: SleeperConfig, existingData: String): String {
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