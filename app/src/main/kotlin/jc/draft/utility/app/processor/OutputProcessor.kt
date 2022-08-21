package jc.draft.utility.app.processor

import jc.draft.utility.app.utility.ProcessContext

interface OutputProcessor<T : ProcessContext> {
    fun handleOutput(context: T)
}