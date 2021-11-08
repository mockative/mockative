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
        }

        val iosMain by getting

        val iosArm64Main by getting {
            dependsOn(iosMain)
        }

//        val iosX64Main by getting {
//            languageSettings {
////                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
//            }
//        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}
