package jc.draft.utility

import jc.draft.utility.data.entities.CachedData
import jc.draft.utility.data.entities.CachedDataEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class CacheableDataTest {

    class TestDataService : CacheableData<String> {
        override fun directory(c: String): String {
            return c;
        }

        override fun refreshData(c: String, existingData: String): String {
            return testJson
        }
    }

    companion object {
        val postgresContainer = PostgreSQLContainer("postgres:14.5")
            .withDatabaseName("testdb")
        val testKey = "test"
        val testJson = """
 {
   "key": "value"      
 }
""".trimIndent()
    }

    @BeforeEach
    fun setUp() {
        postgresContainer.start()
        Database.connect(
            url = postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgresContainer.username,
            password = postgresContainer.password
        )
        transaction {
            addLogger(StdOutSqlLogger)

            TransactionManager.current().exec(
                """
CREATE TABLE IF NOT EXISTS "cached_data" (
    "id" SERIAL PRIMARY KEY,
    "data_type" varchar,
    "timestamp" timestamp,
    "data_key" varchar,
    "data" bytea
);
            """.trimIndent()
            )
        }
    }

    @AfterEach
    fun tearDown() {
        postgresContainer.stop()
    }

    @Test
    fun `test cacheable data interface creates and persists data when none existing`() {
        val testService = TestDataService()
        val data = testService.getData(testKey, false)
        assertNotNull(data)
        assertEquals(testJson, data)

        // validate persisted data matches expected
        transaction {
            val cachedData = CachedDataEntity.find { CachedData.dataKey eq testKey }
            assertEquals(1, cachedData.count())

            val actualData = cachedData.first()
            assertNotNull(actualData)
            assertTrue(actualData.timestamp.isBefore(LocalDateTime.now()))
            assertEquals("TXT", actualData.dataType)
            assertEquals(testKey, actualData.dataKey)
            assertEquals(testJson, String(actualData.data.bytes))
        }
    }

    @Test
    fun `test cacheable data interface creates and persists data when retrieving existing`() {
        val testService = TestDataService()
        // initial fetch to populate existing
        testService.getData(testKey, true)

        // subsequent get data should retrieve from cache
        val data = testService.getData(testKey, false)
        assertNotNull(data)
        assertEquals(testJson, data)

        // validate persisted data matches expected
        transaction {
            val cachedData = CachedDataEntity.find { CachedData.dataKey eq testKey }
            assertEquals(1, cachedData.count())
        }
    }

    @Test
    fun `test cacheable data interface creates and persists data when triggering new fetch`() {
        val testService = TestDataService()
        // initial fetch to populate existing
        testService.getData(testKey, true)

        // subsequent get data should retrieve from retrieve new again
        val data = testService.getData(testKey, true)
        assertNotNull(data)
        assertEquals(testJson, data)

        // validate persisted data matches expected
        transaction {
            val cachedData = CachedDataEntity.find { CachedData.dataKey eq testKey }
            assertEquals(2, cachedData.count())
        }
    }

    @Test
    fun `test that I know how to save a blob column`() {
        transaction {
            val data = CachedDataEntity.new {
                dataType = "TXT"
                timestamp = LocalDateTime.now()
                dataKey = testKey
                data = ExposedBlob(testJson.toByteArray())
            }
            assertEquals(testJson, String(data.data.bytes))
        }
    }
}