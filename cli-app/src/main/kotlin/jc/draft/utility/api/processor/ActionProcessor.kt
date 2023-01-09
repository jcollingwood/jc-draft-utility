package jc.draft.utility.api.processor

import jc.draft.utility.api.utility.ProcessContext

interface ActionProcessor<T : ProcessContext> {
    fun handleAction(context: T)
}