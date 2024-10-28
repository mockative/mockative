plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.2.1"
}

version = "3.0.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.0.21")

    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.21-1.0.26")
}

kotlin {
    jvmToolchain(11)
}

val copySourcesToResources by tasks.registering(Copy::class) {
    from("$rootDir/mockative-test/src")
    into("src/main/resources/src/")
}

tasks.named("processResources") {
    dependsOn(copySourcesToResources)
}

tasks.whenObjectAdded {
    if (name == "sourcesJar") {
        dependsOn(copySourcesToResources)
    }
}

gradlePlugin {
    plugins {
        create("mockative") {
            id = "io.mockative"
            displayName = "Mockative Gradle Plugin"
            implementationClass = "io.mockative.MockativePlugin"
            description = "Gradle Plugin for Mockative"
        }
    }
}
