package jc.draft.utility.league

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

/**
 * Handles data caching and refresh with default 24 hour refresh duration
 *
 * Only required configuration when implementing is to provide the cache directory and the mechansim for refreshing the data
 */
interface CacheableData<C> {
    fun directory(c: C): String

    fun getData(c: C): String {
        // TODO mechanism to force refresh of data
        val existingDataCache = getLatestFile(c)

        existingDataCache?.let { data ->
            if (shouldRefresh(data)) {
                println("refreshing data for $c")
                return refreshAndPersistNewFile(c)
            } else {
                return data.readText()
            }
        } ?: run {
            println("no existing data, retrieving new for $c")
            return refreshAndPersistNewFile(c)
        }
    }

    fun refreshData(c: C): String

    fun refreshAndPersistNewFile(c: C): String {
        val data = refreshData(c)
        File("${dataDirectory(c)}/${LocalDateTime.now().format(fileNameDateFormatter)}.json").writeText(
            data
        )
        return data
    }

    fun refreshDurationHours(): Long {
        return 24
    }

    fun shouldRefresh(file: File?): Boolean {
        if (file == null) return true
        // expected file name format to be date time of format YYYYmmddHHmmss, check against configured refresh duration
        val fileName = file.name.replace(".json", "")

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