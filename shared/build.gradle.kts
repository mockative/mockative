plugins {
    kotlin("multiplatform")

    id("com.android.library")

    id("io.mockative") version "3.0.0-SNAPSHOT"
}

version = "1.0.0"
group = "io.mockative"

kotlin {
    jvmToolchain(11)

    js(IR) {
        browser()
        nodejs()
    }

    wasmWasi {
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
                implementation(project(":mockative"))
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

mockative {
    optIn("kotlin.ExperimentalUnsignedTypes")

    forPackage("io.github") {
        optIn("kotlin.ExperimentalStdlibApi")

        type("OptInType") {
            optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }
}
