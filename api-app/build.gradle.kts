val ktorVersion = "2.1.0"
plugins {
    id("jc.draft.utility.kotlin-application-conventions")
//    application //to run JVM part
    kotlin("plugin.serialization") version "1.6.10"
//    kotlin("jvm") version "1.8.20"
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

    implementation(project(":utility"))
    implementation(kotlin("stdlib-jdk8"))

}

application {
    // Define the main class for the application.
    mainClass.set("jc.draft.utility.app.ApiAppKt")
}
//val compileKotlin: KotlinCompile by tasks
//compileKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}
//val compileTestKotlin: KotlinCompile by tasks
//compileTestKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}