import com.github.gradle.node.npm.task.NpxTask

val ktorVersion = "2.1.0"

plugins {
    id("jc.draft.utility.kotlin-application-conventions")
    id("io.ktor.plugin") version "2.1.0"
    id("com.github.node-gradle.node") version "7.0.2"
    kotlin("plugin.serialization") version "2.0.20"
}

application {
    mainClass.set("jc.draft.utility.api.DraftUtilityServerKt")
}

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

tasks.register<NpxTask>("tailwind") {
    command.set("tailwindcss")
    args.addAll("-o", "src/main/resources/static/styles.css")

    inputs.dir("src")
    inputs.files("tailwind.config.js")
    outputs.files("src/main/resources/static/styles.css")
}

tasks.named("classes") {
    dependsOn("tailwind")
}
