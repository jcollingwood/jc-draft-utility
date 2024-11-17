package jc.draft.utility

import jc.draft.utility.data.entities.CachedData
import jc.draft.utility.data.entities.CachedDataEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.two.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
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
    companion object {
        private val log = KotlinLogging.logger {}
        private val mutex = Mutex()
    }

    fun directory(c: C): String
    fun dataType(): CacheDataType {
        return CacheDataType.TXT
    }

    /**
     * get data logic, handling cache retrieval, refresh, and initial fetch lifecycle
     */
    fun getData(c: C, fetchNew: Boolean = false): String {
        val existingDataCache = if (fetchNew) null else getLatestData(c)

        existingDataCache?.let { data ->
            if (shouldRefresh(data)) {
                log.info("refreshing data for $c")
                val refresh: (C) -> String = { c -> refreshData(c, String(data.data.bytes)) }
                return refreshAndPersistNewFile(refresh, c)
            } else {
                log.info("returning cached data for $c")
                return String(data.data.bytes)
            }
        } ?: run {
            if (fetchNew)
                log.info("refetch of data explicitly requested for $c")
            else
                log.info("no existing data, retrieving new for $c")
            val refresh: (C) -> String = { c -> refreshDataFirstTime(c) }
            return refreshAndPersistNewFile(refresh, c)
        }
    }

    /**
     * ensures only single get data triggered at a time, useful for shared data that would be access concurrently
     */
    fun lockedGetData(c: C, fetchNew: Boolean = false): String {
        return runBlocking { mutex.withLock { getData(c, fetchNew) } }
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
        transaction {
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