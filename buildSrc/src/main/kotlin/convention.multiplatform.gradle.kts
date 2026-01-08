@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    jvmToolchain(11)

    jvm()

    androidLibrary {
        compileSdk = 34
        namespace = "io.mockative"
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
