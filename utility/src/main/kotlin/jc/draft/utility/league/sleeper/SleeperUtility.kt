package jc.draft.utility.league.sleeper

import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import jc.draft.utility.league.CacheDataType
import jc.draft.utility.league.CacheableData
import jc.draft.utility.league.Position
import jc.draft.utility.league.Status
import jc.draft.utility.league.client
import jc.draft.utility.league.jsonParser
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SleeperPlayer(
    @SerialName("injury_status") val injuryStatus: String? = null,
    val active: Boolean? = null,
//    val fantasy_positions: List<String> = emptyList(),
    val position: String? = null,
    val team: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("espn_id") val espnId: Int? = null,
    @SerialName("yahoo_id") val yahoo_id: Int? = null,
    // is starting to be set by service, not part of retrieved data
    var isStarting: Boolean = false
)

fun getSleeperPosition(position: String?): Position {
    return when (position) {
        "QB" -> Position.QB
        "RB" -> Position.RB
        "WR" -> Position.WR
        "TE" -> Position.TE
        "K" -> Position.K
        "DEF" -> Position.DST
        else -> Position.Unknown
    }
}

fun getSleeperStatus(status: String?): Status {
    return when (status) {
        "",
        null,
        "Active" -> Status.Active

        "Questionable" -> Status.Questionable
        "Doubtful" -> Status.Doubtful
        "Out" -> Status.Out
        "PUP" -> Status.PUP
        "IR" -> Status.IR
        else -> {
            println("unknown sleeper status: $status")
            Status.Unknown
        }
    }
}

data class SleeperConfig(val id: String?)

fun getSleeperPlayers(fetchNew: Boolean = false): Map<String, SleeperPlayer>? {
    return jsonParser.decodeFromString<Map<String, SleeperPlayer>>(
        SleeperPlayersData().getData(
            c = SleeperConfig("1"),
            fetchNew = fetchNew
        )
    )
}

/**
 * Sleeper is public and readonly so no auth needed
 */
class SleeperPlayersData : CacheableData<SleeperConfig> {
    override fun directory(c: SleeperConfig): String {
        return "sleeper/players"
    }

    override fun dataType(): CacheDataType {
        return CacheDataType.JSON
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
                HttpStatusCode.OK -> println("successfully retrieved sleeper player data ${response.bodyAsText()}")
                else -> println("failed to retrieve sleeper player data : \nstatus:${response.status}\nbody:${response.bodyAsText()}")
            }
            return@runBlocking response.bodyAsText()
        }
    }
}