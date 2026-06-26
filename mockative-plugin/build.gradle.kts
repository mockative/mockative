plugins {
    kotlin("jvm")

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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.3.20")

    implementation("com.android.tools.build:gradle:9.2.0")

    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.7")
}

kotlin {
    jvmToolchain(11)
}

val generateVersionProperties by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/mockative-version")
    val projectVersion = project.version.toString()
    inputs.property("version", projectVersion)
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().file("mockative-version.properties").asFile
        file.parentFile.mkdirs()
        file.writeText("version=$projectVersion\n")
    }
}

sourceSets.main {
    resources.srcDir(generateVersionProperties.map { it.outputs.files.singleFile })
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
