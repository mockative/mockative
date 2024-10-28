@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(17)

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    watchosDeviceArm64()

    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()

    macosX64()
    macosArm64()

    linuxArm64()
    linuxX64()

    mingwX64()

    wasmJs {
        browser()
        nodejs()
        generateTypeScriptDefinitions()
    }

    wasmWasi {
        nodejs()
    }

    applyDefaultHierarchyTemplate()
}
