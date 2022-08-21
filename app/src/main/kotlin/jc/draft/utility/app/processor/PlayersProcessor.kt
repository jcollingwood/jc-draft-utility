package jc.draft.utility.app.processor

import jc.draft.utility.app.domain.player.Player
import jc.draft.utility.app.service.PlayerService
import jc.draft.utility.app.utility.AppContext
import jc.draft.utility.app.utility.ConsoleStyling.CYAN
import jc.draft.utility.app.utility.ConsoleStyling.RESET
import jc.draft.utility.app.utility.ConsoleStyling.YELLOW
import jc.draft.utility.app.utility.MenuEnum
import jc.draft.utility.app.utility.OPTIONS
import jc.draft.utility.app.utility.ProcessContext

const val LIST = "list"
const val SEARCH = "search"
const val BACK = "back"

val VALID_PLAYERS_INPUTS = listOf(OPTIONS, BACK, LIST, SEARCH)

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
            SEARCH -> context.players = handleSearch()
        }
    }

    private fun handleSearch(): List<Player> {
        print("enter search value: ")
        val searchVal = readln()
        return PlayerService().getPlayers().filter { it.name.contains(searchVal) }
    }
}

class PlayersOutputProcessor : OutputProcessor<PlayerContext> {
    override fun handleOutput(context: PlayerContext) {
        when (context.appContext.inputVal) {
            BACK -> println("switched to sub menu [$CYAN$PLAYERS$RESET]")
            LIST, SEARCH -> context.players.forEach { printPlayer(it) }
            OPTIONS -> println("valid options in this menu are $CYAN$VALID_PLAYERS_INPUTS$RESET")
        }
    }

    private fun value(value: String): String = "$YELLOW$value$RESET"

    private fun printPlayer(player: Player) {
        println("|   ${player.id}")
        println("|   Name: ${value(player.name)}   Pos: ${value(player.position.name)}")
        println("|   Team: ${value(player.team.teamName)}   Bye: ${value(player.bye.toString())}")
        println("|   Rank: ${value(player.rank.toString())}   ADP: ${value(player.adp.toString())}")
        println("|")
    }
}