plugins {
    id("convention.multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    sourceSets {
        // Common
        named("commonMain") {
            kotlin.srcDirs("$buildDir/generated/mockative-code-generator")
        }

        named("jvmMain") {
            dependencies {
                implementation("net.bytebuddy:byte-buddy:1.14.5")
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.native.FreezingIsDeprecated")
            }
        }
    }
}

afterEvaluate {
    kotlin.targets["metadata"].compilations.forEach { compilation ->
        compilation.compileTaskProvider {
            compilation.compileDependencyFiles = files(
                compilation.compileDependencyFiles.filterNot { it.absolutePath.endsWith("klib/common/stdlib") }
            )
        }
    }
}
