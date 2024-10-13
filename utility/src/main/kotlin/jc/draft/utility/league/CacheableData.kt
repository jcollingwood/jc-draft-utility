package jc.draft.utility.league

import jc.draft.utility.data.entities.CachedData
import jc.draft.utility.data.entities.CachedDataEntity
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

enum class CacheDataType(val extension: String) {
    JSON(".json"), XML(".xml"), TXT(".txt")
}

/**
 * Handles data caching and refresh with default 24 hour refresh duration
 *
 * Only required configuration when implementing is to provide the cache directory and the mechanism for refreshing the data
 */
interface CacheableData<C> {
    fun directory(c: C): String
    fun dataType(): CacheDataType {
        return CacheDataType.TXT
    }

    fun getData(c: C, fetchNew: Boolean = false): String {
        println("getting data for $c")
        val existingDataCache = if (fetchNew) null else getLatestData(c)

        existingDataCache?.let { data ->
            if (shouldRefresh(data)) {
                println("refreshing data for $c")
                val refresh: (C) -> String = { c -> refreshData(c, data.data.inputStream.readAllBytes().toString()) }
                return refreshAndPersistNewFile(refresh, c)
            } else {
                println("returning cached data")
                println(data.data.inputStream.readAllBytes().toString())
                return data.data.inputStream.readAllBytes().toString()
            }
        } ?: run {
            if (fetchNew)
                println("refetch of data explicitly requested for $c")
            else
                println("no existing data, retrieving new for $c")
            val refresh: (C) -> String = { c -> refreshDataFirstTime(c) }
            return refreshAndPersistNewFile(refresh, c)
        }
    }

    /**
     * optionally override behavior to fetch data for first time differently, defaults to normal fetch behavior
     */
    fun refreshDataFirstTime(c: C): String {
        return refreshData(c)
    }

    fun refreshData(c: C, existingData: String = ""): String

    fun refreshAndPersistNewFile(refreshFunc: (C) -> String, c: C): String {
        val data = refreshFunc(c)
//        File(
//            "${dataDirectory(c)}/${
//                LocalDateTime.now().format(fileNameDateFormatter)
//            }${dataType().extension}"
//        ).writeText(
//            data
//        )
        transaction {
            addLogger(StdOutSqlLogger)
            CachedDataEntity.new {
                dataType = dataType().name
                timestamp = LocalDateTime.now()
                dataKey = directory(c)
                this.data = ExposedBlob(data.toByteArray())
            }
        }
        return data
    }

    fun refreshDurationHours(): Long {
        return 24
    }

    fun shouldRefresh(data: CachedDataEntity?): Boolean {
        if (data == null) return true
        return data.timestamp.isBefore(LocalDateTime.now().minusHours(refreshDurationHours()))
    }

    fun getLatestData(c: C): CachedDataEntity? {
        return transaction {
            CachedDataEntity
                .find { CachedData.dataKey eq directory(c) }
                .orderBy(CachedData.timestamp to SortOrder.DESC)
                .firstOrNull()
        }
    }
}