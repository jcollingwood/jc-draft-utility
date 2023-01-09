package jc.draft.utility.api.processor

import jc.draft.utility.api.utility.ProcessContext

interface OutputProcessor<T : ProcessContext> {
    fun handleOutput(context: T)
}