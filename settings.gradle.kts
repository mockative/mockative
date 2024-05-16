pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("multiplatform") version "2.0.0-RC3" apply false
        kotlin("plugin.allopen") version "2.0.0-RC3" apply false

        id("com.google.devtools.ksp") version "2.0.0-RC3-1.0.16" apply false
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
