package jc.draft.utility.api.processor

import jc.draft.utility.api.utility.ConsoleStyling.CYAN
import jc.draft.utility.api.utility.ConsoleStyling.RESET
import jc.draft.utility.api.utility.EXIT_INPUT
import jc.draft.utility.api.utility.ProcessContext
import jc.draft.utility.api.utility.menuPrompt

interface InputProcessor<T : ProcessContext> {

    fun handleInput(context: T) {
        var input: String
        input = takeInput(context)
        while (
            input != "" &&
            input != EXIT_INPUT &&
            !validateInput(context, input)
        ) {
            println("invalid input [$CYAN$input$RESET]")
            println("valid inputs: $CYAN${getValidInputs()}$RESET")
            input = takeInput(context)
        }
        context.appContext.inputVal = input
    }

    fun takeInput(context: T): String
    fun validateInput(context: T, input: String): Boolean
    fun getValidInputs(): List<String>
}

abstract class AbstractionInputProcessor<T : ProcessContext> : InputProcessor<T> {
    override fun takeInput(context: T): String {
        menuPrompt(context.appContext, "")
        return readln().trim()
    }

    override fun validateInput(context: T, input: String): Boolean {
        return input == EXIT_INPUT || getValidInputs().contains(input)
    }
}