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

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    js(IR) {
        browser()
        nodejs()
    }

    jvm()

    androidLibrary {
        minSdk = 21
        compileSdk = 36
        namespace = "io.mockative"

        // Opt-in to enable and configure host-side (unit) tests
        withHostTest {
            isIncludeAndroidResources = true
        }

        // Opt-in to enable and configure device-side (instrumented) tests
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            execution = "HOST"
        }
    }

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
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
            }
        }

        commonTest {
            dependencies {
                implementation("io.mockative:mockative:3.1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
                implementation(kotlin("test"))
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }

        androidMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
            }
        }

        getByName("androidHostTest") {
            dependencies {
            }
        }

        getByName("androidHostTest") {
            dependencies {
                // implementation(kotlin("test-junit"))
                // implementation("junit:junit:4.13.2")
            }
        }
    }
}

mockative {
    // Uncomment when version includes multimodule handling feature
//    isMultimodule = true
}