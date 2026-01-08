@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("multiplatform")

    id("com.android.kotlin.multiplatform.library")

    id("io.mockative") version "3.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

version = "1.0.0"
group = "io.mockative"

detekt {
    buildUponDefaultConfig = true

    source.setFrom("src/**/kotlin")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    reports {
        html.required.set(true)
        xml.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}

kotlin {
    jvmToolchain(11)

    js(IR) {
        browser()
        nodejs()
    }

    jvm()

    androidTarget()

    val iosX64 = iosX64()
    val iosArm64 = iosArm64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    configure(listOf(iosX64, iosArm64, iosSimulatorArm64)) {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.configure {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            }
        }

        commonTest.configure {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
                implementation(kotlin("test"))
            }
        }

        jvmTest.configure {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }

        androidUnitTest.configure {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
    }
}

android {
    compileSdk = 33
    namespace = "io.mockative"

//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        testOptions {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        androidTestImplementation("androidx.test:runner:1.5.2")
        androidTestUtil("androidx.test:orchestrator:1.4.2")
    }
}
