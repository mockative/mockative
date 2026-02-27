pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("multiplatform") version "2.3.0" apply false
        kotlin("plugin.allopen") version "2.3.0" apply false

        id("com.google.devtools.ksp") version "2.3.4" apply false

        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }
}

rootProject.name = "mockative"

val mockativeProjects = startParameter.projectProperties["mockative.projects"]
if (mockativeProjects != null) {
    mockativeProjects.split(",").forEach { project ->
        include(project)
    }
} else {
    include(":shared")
    include(":example")
    include(":example:feature-one")
    include(":example:feature-two")
    include(":mockative")
    include(":mockative-processor")
    include(":mockative-plugin")
    include(":mockative-test")
}
