package jc.draft.utility.league.espn

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val jsonParser = Json { ignoreUnknownKeys = true }
const val ROSTER_DATA_DIRECTORY = ".data_cache"
val FILE_NAME_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmSS")
const val ESPN_URL = "https://lm-api-reads.fantasy.espn.com/apis/v3/games"

// espn cookies (can be found in Chrome under application/cookies with same name)
// note: remove surrounding {} from swid
// note: ensure you grab url encoded value (or url encode in code here)
val SWID = System.getenv("ESPN_SWID")
val ESPN_S2 = System.getenv("ESPN_S2")

val client = HttpClient(CIO)

fun getLeagueUrl(config: LeagueConfig): String {
    return "${ESPN_URL}/ffl/seasons/${config.year}/segments/0/leagues/${config.leagueId}"
}

fun testEspnUrl() {
    listOf(federationLeague, bfflLeague, clayLeague, workLeague).forEach {
        runBlocking {
            // retrieve league roster
            val players = retrieveLeagueRoster(it)

            println("${it.leagueName} League")
            players?.forEach {
                if (it.injuryStatus == null || it.injuryStatus == "ACTIVE")
                    println(it.fullName)
                else
                    println("${it.fullName} : ${it.injuryStatus}")
            }
            println()
        }
    }
}

suspend fun retrieveLeagueRoster(leagueConfig: LeagueConfig): List<EspnPlayer>? {
    // check db to determine if fresh roster needs retrieval
    val existingDataCache = getLatestFile(leagueConfig)

    existingDataCache?.let { data ->
        // read data from file, getting fresh data if stale
        println("data exists ${data.name}")
        return parsePlayersFromJson(data.readText(), leagueConfig.teamId)
    } ?: run {
        // retrieve data and persist new record
        println("no existing data, retrieving new")
        val data = retrieveFreshLeagueRoster(leagueConfig)
        persistNewFile(leagueConfig, data)
        return parsePlayersFromJson(data, leagueConfig.teamId)
    }
}

/* retrieves latest timestamped data cache file for league */
fun getLatestFile(leagueConfig: LeagueConfig): File? {
    Files.createDirectories(Paths.get(ROSTER_DATA_DIRECTORY))
    val leagueCacheDirectory = leagueCacheDirectory(leagueConfig)
    Files.createDirectories(Paths.get(leagueCacheDirectory))
    val cacheDirectory = File(leagueCacheDirectory)
    return cacheDirectory.walk().filter(File::isFile).sortedDescending().firstOrNull()
}

/* saved new file with latest timestamp and data */
fun persistNewFile(leagueConfig: LeagueConfig, data: String) {
    File("${leagueCacheDirectory(leagueConfig)}/${LocalDateTime.now().format(FILE_NAME_DATE_FORMAT)}.json").writeText(
        data
    )
}

/* retrieves directory for league data cache */
val leagueCacheDirectory: (LeagueConfig) -> String =
    { leagueConfig -> "$ROSTER_DATA_DIRECTORY/${leagueConfig.year}_${leagueConfig.leagueName}_${leagueConfig.leagueId}" }

/* deserialize json data */
val parsePlayersFromJson: (String, Int) -> List<EspnPlayer> = { json, teamId ->
    jsonParser.decodeFromString<EspnResponse>(json).teams.filter { it.id == teamId }
        .first().roster?.entries?.map { it.playerPoolEntry?.player }?.filterNotNull() ?: emptyList()
}

/* make api call to retrieve league roster data */
suspend fun retrieveFreshLeagueRoster(leagueConfig: LeagueConfig): String {
    val response: HttpResponse = client.request(getLeagueUrl(leagueConfig)) {
        method = HttpMethod.Get
        url {
            parameters.append("rosterForTeamId", "${leagueConfig.teamId}")
            parameters.append("view", EspnView.Roster.value)
        }
        cookie("swid", SWID)
        cookie("espn_s2", ESPN_S2)
    }
    when (response.status) {
        HttpStatusCode.OK -> println("successfully retrieved league roster data")
        else -> println("failed to retrieve league roster data : \nstatus:${response.status}\nbody:${response.bodyAsText()}")
    }
    return response.bodyAsText()
}
