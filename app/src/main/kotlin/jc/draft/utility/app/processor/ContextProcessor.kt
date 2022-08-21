package jc.draft.utility.app.processor

import jc.draft.utility.app.utility.AppContext
import jc.draft.utility.app.utility.MenuEnum
import jc.draft.utility.app.utility.ProcessContext

open class Processor<T : ProcessContext>(
    val inputProcessor: InputProcessor<T>,
    val actionProcessor: ActionProcessor<T>,
    val outputProcessor: OutputProcessor<T>
) {
    fun process(context: T) {
        inputProcessor.handleInput(context)
        actionProcessor.handleAction(context)
        outputProcessor.handleOutput(context)
    }
}

class PlayerProcessor : Processor<PlayerContext>(
    inputProcessor = PlayersInputProcessor(),
    actionProcessor = PlayersActionProcessor(),
    outputProcessor = PlayersOutputProcessor()
)

class HomeProcessor : Processor<ProcessContext>(
    inputProcessor = HomeInputProcessor(),
    actionProcessor = HomeActionProcessor(),
    outputProcessor = HomeOutputProcessor()
)

class ContextProcessor {
    fun process(context: AppContext) {
        when (context.subMenu) {
            MenuEnum.PLAYERS -> PlayerProcessor().process(PlayerContext(context))
            MenuEnum.HOME -> HomeProcessor().process(ProcessContext(context))
        }
    }
}