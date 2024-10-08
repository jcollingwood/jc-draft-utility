plugins {
    id("jc.draft.utility.kotlin-application-conventions")
    id("jc.draft.utility.kotlin-exposed-conventions")
    kotlin("plugin.serialization") version ("2.0.20")
}

//val ktorVersion: String by project
val ktorVersion = "2.3.12"

dependencies {
    implementation(project(":data"))
    implementation("org.jsoup:jsoup:1.14.3")

    // serialization plugins
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.2")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    if (project.hasProperty("update-player-metadata"))
        mainClass.set("jc.draft.utility.api.PfrPlayerMetadataPersistenceUtilityKt")
    else if (project.hasProperty("update-player-game-stats"))
        mainClass.set("jc.draft.utility.api.PfrPlayerGameStatsPersistenceUtilityKt")
    else
        mainClass.set("jc.draft.utility.api.UtilityMainKt")
}