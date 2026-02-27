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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.3.0")

    implementation("com.android.tools.build:gradle:8.12.3")

    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.4")
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
