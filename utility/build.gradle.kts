plugins {
    id("jc.draft.utility.kotlin-application-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.14.3")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}