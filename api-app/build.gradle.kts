plugins {
    id("jc.draft.utility.kotlin-application-conventions")
    kotlin("plugin.serialization") version "2.0.20"
}

val ktorVersion = "2.1.0"

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")

    // html/css dsl
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-css:1.0.0-pre.810")

    implementation(project(":utility"))

}

application {
    // Define the main class for the application.
    mainClass.set("jc.draft.utility.api.DraftUtilityServerKt")
}
