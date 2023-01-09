package jc.draft.utility.api.processor

import jc.draft.utility.api.utility.ConsoleStyling.CYAN
import jc.draft.utility.api.utility.ConsoleStyling.RESET
import jc.draft.utility.api.utility.MenuEnum
import jc.draft.utility.api.utility.OPTIONS
import jc.draft.utility.api.utility.ProcessContext

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