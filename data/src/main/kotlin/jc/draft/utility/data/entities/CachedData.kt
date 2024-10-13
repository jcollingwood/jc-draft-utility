package jc.draft.utility.data.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object CachedData : IntIdTable("cached_data") {
    val dataType = varchar("data_type", 255)
    val timestamp = datetime("timestamp")
    val dataKey = varchar("data_key", 255)
    val data = blob("data")
}

class CachedDataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CachedDataEntity>(CachedData)

    var dataType by CachedData.dataType
    var timestamp by CachedData.timestamp
    var dataKey by CachedData.dataKey
    var data by CachedData.data
}