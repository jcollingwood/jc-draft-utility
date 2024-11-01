package jc.draft.utility.league

import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter

val jsonParser = Json { ignoreUnknownKeys = true }

const val ROSTER_DATA_DIRECTORY = ".data_cache"
val FILE_NAME_DATE_FORMAT = "yyyyMMddHHmm"
val fileNameDateFormatter = DateTimeFormatter.ofPattern(FILE_NAME_DATE_FORMAT)
