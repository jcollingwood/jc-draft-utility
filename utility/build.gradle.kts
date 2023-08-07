plugins {
    id("jc.draft.utility.kotlin-application-conventions")
    id("jc.draft.utility.kotlin-exposed-conventions")
}

val exposedVersion = "0.40.1"

dependencies {
    implementation(project(":data"))
    implementation("org.jsoup:jsoup:1.14.3")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    if(project.hasProperty("update-player-metadata"))
        mainClass.set("jc.draft.utility.api.PfrPlayerMetadataPersistenceUtilityKt")
    else if(project.hasProperty("update-player-game-stats"))
        mainClass.set("jc.draft.utility.api.PfrPlayerGameStatsPersistenceUtilityKt")
    else
        mainClass.set("jc.draft.utility.api.UtilityMainKt")
}