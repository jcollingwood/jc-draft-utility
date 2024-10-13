plugins {
    id("jc.draft.utility.kotlin-library-conventions")
    id("jc.draft.utility.kotlin-exposed-conventions")
    id("org.flywaydb.flyway") version "9.16.0"
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.2")
    // not getting pulled in transitively from exposed-conventions...
    implementation("org.jetbrains.exposed:exposed-java-time:0.55.0")
}

flyway {
    url = "jdbc:postgresql://localhost:5432/jcdraftutility"
    user = System.getenv("PSQL_USER") ?: "postgres"
    password = System.getenv("PSQL_PASSWORD") ?: "postgres"
    locations = arrayOf("classpath:db/migrations")
}