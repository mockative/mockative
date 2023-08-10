import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
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

    androidTarget()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
        System.getProperty("os.arch") == "aarch64"-> ::iosSimulatorArm64
        else -> ::iosX64
    }

    iosTarget("ios") {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        named("commonTest") {
            dependencies {
                implementation(project(":mockative"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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

//        named("iosMain")
        named("iosTest") {
            dependencies {
                implementation(project(":mockative"))
            }

            languageSettings {
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }

            kotlin.srcDir(File(buildDir, "generated/ksp/ios/iosTest/kotlin"))
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

ksp {
    arg("mockative.stubsUnitByDefault", "true")
}
