package jc.draft.utility.api.processor

import jc.draft.utility.api.domain.player.Player
import jc.draft.utility.api.service.PlayerSearchCriteria
import jc.draft.utility.api.service.PlayerService
import jc.draft.utility.api.utility.AppContext
import jc.draft.utility.api.utility.ConsoleStyling.CYAN
import jc.draft.utility.api.utility.ConsoleStyling.RESET
import jc.draft.utility.api.utility.ConsoleStyling.YELLOW
import jc.draft.utility.api.utility.MenuEnum
import jc.draft.utility.api.utility.OPTIONS
import jc.draft.utility.api.utility.ProcessContext

const val LIST = "list"
const val PLAYER = "player"
const val SEARCH = "search"
const val HISTORY = "history"
const val BACK = "back"

val VALID_PLAYERS_INPUTS = listOf(OPTIONS, BACK, LIST, PLAYER, SEARCH, HISTORY)

class PlayerContext(
    appContext: AppContext,
    var players: List<Player> = emptyList()
) : ProcessContext(appContext)

class PlayersInputProcessor : AbstractionInputProcessor<PlayerContext>() {
    override fun getValidInputs(): List<String> {
        return VALID_PLAYERS_INPUTS
    }
}

class PlayersActionProcessor : ActionProcessor<PlayerContext> {
    override fun handleAction(context: PlayerContext) {
        when (context.appContext.inputVal) {
            BACK -> context.appContext.subMenu = MenuEnum.HOME
            LIST -> context.players = PlayerService().getPlayers()
            PLAYER -> context.players = handlePlayerLookup()
            SEARCH -> context.players = handleSearch()
            HISTORY -> context.players = handlePlayerHistoryLookup()
        }
    }

    private fun handleSearch(): List<Player> {
        print("enter search value: ")
        val searchVal = readln()
        return PlayerService().getPlayers(PlayerSearchCriteria(searchVal))
    }

    private fun handlePlayerLookup(): List<Player> {
        print("enter player id: ")
        val playerId = readln()
        return listOf(PlayerService().getPlayerById(playerId.toInt()))
    }

    private fun handlePlayerHistoryLookup(): List<Player> {
        print("enter player id: ")
        val playerId = readln()
        return PlayerService().getPlayerHistoryById(playerId.toInt())
    }
}

class PlayersOutputProcessor : OutputProcessor<PlayerContext> {
    override fun handleOutput(context: PlayerContext) {
        when (context.appContext.inputVal) {
            BACK -> println("switched to sub menu [$CYAN$PLAYERS$RESET]")
            LIST, PLAYER, SEARCH, HISTORY -> context.players.forEach { printPlayer(it) }
            OPTIONS -> println("valid options in this menu are $CYAN$VALID_PLAYERS_INPUTS$RESET")
        }
    }

    private fun value(value: String): String = "$YELLOW$value$RESET"

    private fun printPlayer(player: Player) {
        println("|   ${player.id}")
        println("|   Name: ${value(player.name)}   Pos: ${value(player.position.name)}")
        println("|   Team: ${value(player.team.teamName)}   Bye: ${value(player.bye.toString())}")
        println("|   Rank: ${value(player.rank.toString())}   ADP: ${value(player.adp.toString())}")
        println("|   Version: ${value(player.version.toString())}")
        println("|")
    }
}