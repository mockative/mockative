plugins {
    kotlin("jvm")

//    id("com.gradle.plugin-publish") version "1.2.1"

    `java-gradle-plugin`

    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.0.21")

    implementation("com.android.tools.build:gradle:8.5.2")

    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.21-1.0.26")
}

kotlin {
    jvmToolchain(11)
}

val copySourcesToResources by tasks.registering(Copy::class) {
    from("$rootDir/mockative-prefab/src")
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
    website = "https://github.com/mockative/mockative"
    vcsUrl = "https://github.com/mockative/mockative.git"

    plugins {
        create("mockative") {
            id = "io.mockative"
            displayName = "Mockative Gradle Plugin"
            description = "Gradle Plugin for Mockative"
            tags = listOf("testing", "mocking", "mock", "kmp", "native")
            implementationClass = "io.mockative.MockativePlugin"
        }
    }
}
