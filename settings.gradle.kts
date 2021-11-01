pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("multiplatform") version "1.5.31" apply false
        id("com.google.devtools.ksp") version "1.5.31-1.0.0" apply false
    }
}

rootProject.name = "Mockative"
include(":shared")
include(":mockative")
include(":mockative-processor")
