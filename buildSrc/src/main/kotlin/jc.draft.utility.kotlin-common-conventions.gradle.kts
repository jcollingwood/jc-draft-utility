plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    constraints {
        // Define dependency versions as constraints
        implementation("org.apache.commons:commons-text:1.9")
    }

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:4.0.0-beta-2")
    implementation("ch.qos.logback:logback-classic:1.5.11")

    testImplementation(kotlin("test"))
    testImplementation("io.github.microutils:kotlin-logging-jvm:4.0.0-beta-2")
    testImplementation("ch.qos.logback:logback-classic:1.5.11")
}

tasks.test {
    useJUnitPlatform()
}
