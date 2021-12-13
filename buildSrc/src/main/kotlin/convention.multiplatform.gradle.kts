plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    js {
        browser()
        nodejs()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    watchosArm32()
    watchosArm64()
    watchosX86()
    watchosX64()
    watchosSimulatorArm64()

    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()

    macosX64()
    macosArm64()

    linuxArm64()
    linuxArm32Hfp()
    linuxX64()

    mingwX64()
    mingwX86()

    wasm32()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        // Common
        val commonMain by getting

        // JVM
        val jvmMain by getting

        // JS
        val jsMain by getting

        // Native
        val nativeMain by creating { dependsOn(commonMain) }

        // Darwin (iOS, watchOS, tvOS, macOS)
        val darwinMain by creating { dependsOn(nativeMain) }

        // iOS
        val iosMain by creating { dependsOn(darwinMain) }

        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

        // watchOS
        val watchosMain by creating { dependsOn(darwinMain) }

        val watchosArm32Main by getting { dependsOn(watchosMain) }
        val watchosArm64Main by getting { dependsOn(watchosMain) }
        val watchosX86Main by getting { dependsOn(watchosMain) }
        val watchosX64Main by getting { dependsOn(watchosMain) }
        val watchosSimulatorArm64Main by getting { dependsOn(watchosMain) }

        // tvOS
        val tvosMain by creating { dependsOn(darwinMain) }

        val tvosArm64Main by getting { dependsOn(tvosMain) }
        val tvosX64Main by getting { dependsOn(tvosMain) }
        val tvosSimulatorArm64Main by getting { dependsOn(tvosMain) }

        // macOS
        val macosMain by creating { dependsOn(darwinMain) }

        val macosX64Main by getting { dependsOn(macosMain) }
        val macosArm64Main by getting { dependsOn(macosMain) }

        // Linux
        val linuxMain by creating { dependsOn(nativeMain) }

        val linuxArm64Main by getting { dependsOn(linuxMain) }
        val linuxArm32HfpMain by getting { dependsOn(linuxMain) }
        val linuxX64Main by getting { dependsOn(linuxMain) }

        // mingw (Windows)
        val mingwMain by creating { dependsOn(nativeMain) }

        val mingwX64Main by getting { dependsOn(mingwMain) }
        val mingwX86Main by getting { dependsOn(mingwMain) }

        // wasm
        val wasmMain by creating { dependsOn(commonMain) }

        val wasm32Main by getting { dependsOn(wasmMain) }
    }
}