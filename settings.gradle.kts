pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("multiplatform") version "1.8.20" apply false
        id("com.google.devtools.ksp") version "1.8.20-1.0.11" apply false
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
}