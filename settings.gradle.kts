pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://www.jetbrains.com/intellij-repository/snapshots")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide-plugin-dependencies")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }

    plugins {
        kotlin("multiplatform") version "2.0.21" apply false
        kotlin("plugin.allopen") version "2.0.21" apply false

        id("com.google.devtools.ksp") version "2.0.255-SNAPSHOT" apply false

//        id("io.mockative") version "1.0.0-SNAPSHOT" apply false
    }
}

rootProject.name = "mockative"

if (startParameter.projectProperties.containsKey("check_publication")) {
    include(":tools:check-publication")
} else {
    include(":shared")
    include(":mockative")
    include(":mockative-processor")
    include(":mockative-code-generator")
    include(":mockative-plugin")
}
include("mockative-test")
