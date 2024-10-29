pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("multiplatform") version "2.0.21" apply false
        kotlin("plugin.allopen") version "2.0.21" apply false

        id("com.google.devtools.ksp") version "2.0.21-1.0.26" apply false

        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"

//        id("io.mockative") version "3.0.0-SNAPSHOT" apply false
    }
}

rootProject.name = "mockative"

if (startParameter.projectProperties.containsKey("check_publication")) {
    include(":tools:check-publication")
} else {
    include(":shared")
    include(":mockative")
    include(":mockative-processor")
    include(":mockative-plugin")
    include(":mockative-test")
}
