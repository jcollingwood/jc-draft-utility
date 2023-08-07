package jc.draft.utility.data.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object Players : IntIdTable() {
    val name = varchar("name", 255)
    val position = varchar("position", 255)
    val pfrId = varchar("pfrid", 255)
}

class PlayerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerEntity>(Players)

    var name by Players.name
    var position by Players.position
    var pfrId by Players.pfrId
}