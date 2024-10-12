package jc.draft.utility.api

import io.ktor.server.application.Application
import jc.draft.utility.data.jcDraftUtilityDatabase

fun Application.configureDatabase() {
    // connects to defined database
    jcDraftUtilityDatabase()
}