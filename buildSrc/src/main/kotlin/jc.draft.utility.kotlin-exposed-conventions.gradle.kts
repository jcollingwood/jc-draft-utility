import gradle.kotlin.dsl.accessors._e955592cfcca1783c48ac959ec339844.implementation
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

val exposedVersion = "0.40.1"

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
}
