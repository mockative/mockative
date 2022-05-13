pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("multiplatform") version "1.6.20" apply false
        id("com.google.devtools.ksp") version "1.6.20-1.0.5" apply false
    }
}

rootProject.name = "mockative"

include(":shared")
include(":mockative")
include(":mockative-processor")
include(":mockative-test")
include(":mockative-code-generator")

if (startParameter.projectProperties.containsKey("check_publication")) {
    include(":tools:check-publication")
}