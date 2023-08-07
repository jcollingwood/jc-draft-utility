package jc.draft.utility.data

import jc.draft.utility.data.entities.PlayerEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val repo = PlayerRepository()
    repo.testQuery()
}

fun databaseConnect() {
    Database.connect(
        url = System.getenv("PSQL_CONN_URL") ?: "jdbc:postgresql://localhost:5432/jcdraftutility",
        driver = "org.postgresql.Driver",
        user = System.getenv("PSQL_USER") ?: "postgres",
        password = System.getenv("PSQL_PASSWORD") ?: "postgres"
    )
}

class PlayerRepository {

    fun testQuery() {
        databaseConnect()

        var tempId = 0

        transaction {
            print("txn 1")
            addLogger(StdOutSqlLogger)

            val testPlyr = PlayerEntity.new {
                name = "Test Player"
                position = "TE"
                pfrId = "fakeId"
            }
            tempId = testPlyr.id.value
            print(testPlyr.name)
        }

        transaction {
            print("txn 2")
            addLogger(StdOutSqlLogger)

            val testPlyr = PlayerEntity[tempId]
            print(testPlyr.name)
        }
    }
}