package jc.draft.utility.league

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
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
        // TODO mechanism to force refresh of data
        val existingDataCache = if (fetchNew) null else getLatestFile(c)

        existingDataCache?.let { data ->
            if (shouldRefresh(data)) {
                println("refreshing data for $c")
                val refresh: (C) -> String = { c -> refreshData(c, data.readText()) }
                return refreshAndPersistNewFile(refresh, c)
            } else {
                return data.readText()
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
        File(
            "${dataDirectory(c)}/${
                LocalDateTime.now().format(fileNameDateFormatter)
            }${dataType().extension}"
        ).writeText(
            data
        )
        return data
    }

    fun refreshDurationHours(): Long {
        return 24
    }

    fun shouldRefresh(file: File?): Boolean {
        if (file == null || !file.name.contains(dataType().extension)) return true
        // expected file name format to be date time of format YYYYmmddHHmmss, check against configured refresh duration
        val fileName = file.name.replace(dataType().extension, "")

        if (fileName.length != FILE_NAME_DATE_FORMAT.length) println("invalid file name: ${file.name}")
        val fileNameDate = LocalDateTime.parse(fileName, fileNameDateFormatter)

        return fileNameDate.isBefore(LocalDateTime.now().minusHours(refreshDurationHours()))
    }

    fun getLatestFile(c: C): File? {
        return dataDirectory(c).walk().filter(File::isFile).sortedDescending().firstOrNull()
    }

    fun dataDirectory(c: C): File {
        Files.createDirectories(Paths.get(ROSTER_DATA_DIRECTORY))
        val subCacheDirectory = "${ROSTER_DATA_DIRECTORY}/${directory(c)}"
        Files.createDirectories(Paths.get(subCacheDirectory))
        return File(subCacheDirectory)
    }
}