package jc.draft.utility.api

import jc.draft.utility.api.processor.ContextProcessor
import jc.draft.utility.api.utility.AppContext


fun main() {
    val context = AppContext()
    val processor = ContextProcessor()

    while (!context.shouldExit) {
        processor.process(context)
    }
}
