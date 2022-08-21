package jc.draft.utility.app.processor

import jc.draft.utility.app.utility.ConsoleStyling.CYAN
import jc.draft.utility.app.utility.ConsoleStyling.RESET
import jc.draft.utility.app.utility.MenuEnum
import jc.draft.utility.app.utility.OPTIONS
import jc.draft.utility.app.utility.ProcessContext

const val PLAYERS = "players"

val VALID_INPUTS = listOf(OPTIONS, PLAYERS)

class HomeInputProcessor : AbstractionInputProcessor<ProcessContext>() {
    override fun getValidInputs(): List<String> {
        return VALID_INPUTS
    }
}

class HomeActionProcessor : ActionProcessor<ProcessContext> {
    override fun handleAction(context: ProcessContext) {
        if (context.appContext.inputVal == PLAYERS)
            context.appContext.subMenu = MenuEnum.PLAYERS
    }
}

class HomeOutputProcessor : OutputProcessor<ProcessContext> {
    override fun handleOutput(context: ProcessContext) {
        if (context.appContext.inputVal == OPTIONS)
            println("valid options in this menu are $CYAN$VALID_INPUTS$RESET")
        else if (context.appContext.inputVal == PLAYERS)
            println("switched to sub menu [$CYAN$PLAYERS$RESET]")
    }
}