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
            dependencies {
                implementation(kotlin("reflect"))
            }
        }

        named("jvmMain") {
            dependencies {
                implementation("org.objenesis:objenesis:3.3")
                implementation("org.javassist:javassist:3.29.2-GA")
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
