plugins {
    kotlin("multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    jvm()
    iosX64("ios")
    iosArm64()
    js {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
            }

            kotlin.srcDirs("$buildDir/generated/mockative-code-generator")
        }

        val iosMain by getting

        val iosArm64Main by getting {
            dependsOn(iosMain)
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}
