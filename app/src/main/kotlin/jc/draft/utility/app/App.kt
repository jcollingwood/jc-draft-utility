package jc.draft.utility.app

import jc.draft.utility.app.processor.ContextProcessor
import jc.draft.utility.app.utility.AppContext


fun main() {
    val context = AppContext()
    val processor = ContextProcessor()

    while (!context.shouldExit) {
        processor.process(context)
    }
}
