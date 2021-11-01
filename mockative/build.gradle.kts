plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

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
