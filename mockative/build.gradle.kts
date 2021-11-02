plugins {
    kotlin("multiplatform")

    // Publishing
    `maven-publish`
}

group = properties["project.group"] as String
version = properties["project.version"] as String

kotlin {
    jvm()
    ios()
    js {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}
