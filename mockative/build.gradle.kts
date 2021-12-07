plugins {
    kotlin("multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    js {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
            }

            kotlin.srcDirs("$buildDir/generated/mockative-code-generator")
        }

        val nativeMain by creating { dependsOn(commonMain) }

        val darwinMain by creating { dependsOn(nativeMain) }

        val iosMain by creating { dependsOn(darwinMain) }
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}

afterEvaluate {
    kotlin.targets["metadata"].compilations.forEach { compilation ->
        compilation.compileKotlinTask.doFirst {
            compilation.compileDependencyFiles = files(
                compilation.compileDependencyFiles.filterNot { it.absolutePath.endsWith("klib/common/stdlib") }
            )
        }
    }
}
