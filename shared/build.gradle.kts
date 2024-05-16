plugins {
    kotlin("multiplatform")
    kotlin("plugin.allopen")

    id("com.android.library")

    id("com.google.devtools.ksp")
}

version = "1.0.0"
group = "io.mockative"

kotlin {
    jvmToolchain(17)

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
        named("commonMain") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            }
        }
        named("commonTest") {
            dependencies {
                implementation(project(":mockative"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        named("jvmTest") {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }

        named("androidMain")
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }

        named("androidInstrumentedTest") {
            dependsOn(androidUnitTest)
        }

//        named("appleMain")
        named("appleTest") {
            dependencies {
                implementation(project(":mockative"))
            }

            languageSettings {
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }

//        named("jsMain")
        named("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

@Suppress("UnstableApiUsage")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencies {
        androidTestImplementation("androidx.test:runner:1.5.2")
        androidTestUtil("androidx.test:orchestrator:1.4.2")
    }
}

dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, project(":mockative-processor"))
        }
}

val taskIsRunningTest = gradle.startParameter.taskNames
    .any { it == "check" || it.startsWith("test") || it.contains("Test") }

if (taskIsRunningTest) {
    allOpen {
        annotation("io.github.Mockable")
    }
}

ksp {
    arg("io.mockative:mockative:opt-in:io.github.OptInType", "kotlinx.cinterop.ExperimentalForeignApi")
    arg("io.mockative:mockative:opt-in:io.github.*", "kotlin.ExperimentalStdlibApi")
    arg("io.mockative:mockative:opt-in", "kotlin.ExperimentalUnsignedTypes")
}
