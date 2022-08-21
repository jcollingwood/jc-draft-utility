package jc.draft.utility.app.utility

import jc.draft.utility.app.utility.ConsoleStyling.GREEN_BOLD
import jc.draft.utility.app.utility.ConsoleStyling.RESET

const val INIT_INPUT = "INIT"
const val EXIT_INPUT = "exit"
const val OPTIONS = "options"

class AppContext(var inputVal: String = INIT_INPUT, var subMenu: MenuEnum = MenuEnum.HOME) {
    val shouldExit: Boolean
        get() = EXIT_INPUT.equals(inputVal, ignoreCase = true)
}

open class ProcessContext(var appContext: AppContext)

fun menuPrompt(context: AppContext, output: String) =
    print("$GREEN_BOLD${context.subMenu.displayVal}$RESET $output")