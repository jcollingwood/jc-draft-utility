/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("jc.draft.utility.kotlin-application-conventions")
}

dependencies {
    implementation("org.apache.commons:commons-text")
//    implementation(project(":utilities"))
}

application {
    // Define the main class for the application.
    mainClass.set("jc.draft.utility.app.AppKt")
}
