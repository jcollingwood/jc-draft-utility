package jc.draft.utility.api

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jc.draft.utility.api.domain.player.Player
import jc.draft.utility.api.domain.player.PositionEnum
import jc.draft.utility.api.domain.player.TeamEnum
import jc.draft.utility.api.service.PlayerService
import kotlinx.serialization.Serializable


fun main() {
    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
            get("/players") {
                call.respond(PlayerService().getPlayers().map { it.toPlayerDto() })
            }
            get("/players/{playerId}") {
                val playerId = call.parameters["playerId"]?.toInt() ?: error("Invalid player id")
                call.respond(PlayerService().getPlayerById(playerId).toPlayerDto())
            }
        }
    }.start(wait = true)
}

@Serializable
data class PlayerDto(
    val id: Int,
    val name: String,
    val position: PositionEnum,
    val team: TeamEnum,
    val bye: Int,
    val rank: Int,
    val adp: Double
)

fun Player.toPlayerDto() = PlayerDto(
    id = id,
    name = name,
    position = position,
    team = team,
    bye = bye,
    rank = rank,
    adp = adp
)