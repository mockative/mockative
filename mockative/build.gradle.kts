plugins {
    kotlin("multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    jvm()
    ios()
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

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}

afterEvaluate {
    val compilation = kotlin.targets["metadata"].compilations["iosMain"]
    compilation.compileKotlinTask.doFirst {
        compilation.compileDependencyFiles = files(
            compilation.compileDependencyFiles.filterNot { it.absolutePath.endsWith("klib/common/stdlib") }
        )
    }
}
