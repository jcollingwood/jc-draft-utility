package jc.draft.utility.app.processor

import jc.draft.utility.app.utility.ProcessContext

interface ActionProcessor<T : ProcessContext> {
    fun handleAction(context: T)
}