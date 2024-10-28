import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")

    id("com.android.library")

    id("io.mockative") version "1.0.0-SNAPSHOT"
}

version = "1.0.0"
group = "io.mockative"

kotlin {
    jvmToolchain(17)

    js(IR) {
        browser()
        nodejs()
    }

    wasmWasi {
        nodejs()
    }

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation(project(":mockative"))
            }
        }
        named("commonTest") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
//                implementation(project(":mockative-test"))
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

android {
    compileSdk = 33
    namespace = "io.mockative"
}

//@Suppress("UnstableApiUsage")
//android {
//    compileSdk = 33
//    namespace = "io.mockative"
//
////    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//
//    defaultConfig {
//        minSdk = 21
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//
//        testInstrumentationRunnerArguments["clearPackageData"] = "true"
//
//        testOptions {
//            execution = "ANDROIDX_TEST_ORCHESTRATOR"
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//
//    dependencies {
//        androidTestImplementation("androidx.test:runner:1.5.2")
//        androidTestUtil("androidx.test:orchestrator:1.4.2")
//    }
//}

dependencies {
//    configurations
//        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
//        .forEach {
//            add(it.name, project(":mockative-processor"))
//        }

//    println("[build.gradle.kts]")
//    configurations
//        .filter { it.name != "ksp" && it.name.startsWith("ksp") }
//        .forEach {
//            add(it.name, project(":mockative-processor"))
//        }
//    configurations
//        .filter { it.name.contains("ksp", ignoreCase = true) }
//        .forEach {
//            println(it.name)
//            add(it.name, project(":mockative-processor"))
//        }

//    val aaIntellijVersion = "233.13135.103"
//
//    listOf(
//        "com.jetbrains.intellij.platform:util-rt",
//        "com.jetbrains.intellij.platform:util-class-loader",
//        "com.jetbrains.intellij.platform:util-text-matching",
//        "com.jetbrains.intellij.platform:util",
//        "com.jetbrains.intellij.platform:util-base",
//        "com.jetbrains.intellij.platform:util-xml-dom",
//        "com.jetbrains.intellij.platform:core",
//        "com.jetbrains.intellij.platform:core-impl",
//        "com.jetbrains.intellij.platform:extensions",
//        "com.jetbrains.intellij.platform:diagnostic",
//        "com.jetbrains.intellij.java:java-frontback-psi",
//        "com.jetbrains.intellij.java:java-frontback-psi-impl",
//        "com.jetbrains.intellij.java:java-psi",
//        "com.jetbrains.intellij.java:java-psi-impl",
//    ).forEach {
//        implementation("$it:$aaIntellijVersion") { isTransitive = false }
//        depSourceJars("$it:$aaIntellijVersion:sources") { isTransitive = false }
//    }
//
//    val aaKotlinBaseVersion = "2.1.0-dev-5441"
//
//    listOf(
//        "org.jetbrains.kotlin:high-level-api-fir-for-ide",
//        "org.jetbrains.kotlin:high-level-api-for-ide",
//        "org.jetbrains.kotlin:low-level-api-fir-for-ide",
//        "org.jetbrains.kotlin:analysis-api-platform-interface-for-ide",
//        "org.jetbrains.kotlin:symbol-light-classes-for-ide",
//        "org.jetbrains.kotlin:analysis-api-standalone-for-ide",
//        "org.jetbrains.kotlin:high-level-api-impl-base-for-ide",
//        "org.jetbrains.kotlin:kotlin-compiler-common-for-ide",
//        "org.jetbrains.kotlin:kotlin-compiler-fir-for-ide",
//        "org.jetbrains.kotlin:kotlin-compiler-fe10-for-ide",
//        "org.jetbrains.kotlin:kotlin-compiler-ir-for-ide",
//    ).forEach {
//        implementation("$it:$aaKotlinBaseVersion") { isTransitive = false }
//        depSourceJars("$it:$aaKotlinBaseVersion:sources") { isTransitive = false }
//    }
}

//allOpen {
//    annotation("io.mockative.Mockable")
//}

mockative {
    optIn("kotlin.ExperimentalUnsignedTypes")
//    optIn("io.github.OptInType", "kotlinx.cinterop.ExperimentalForeignApi")
//    optIn("io.github.*", "kotlin.ExperimentalStdlibApi")

    forPackage("io.github") {
        optIn("kotlin.ExperimentalStdlibApi")

        type("OptInType") {
            optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }
}
