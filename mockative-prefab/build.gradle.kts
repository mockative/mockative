plugins {
    id("convention.multiplatform")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    sourceSets {
        // Common
        commonMain.configure {
            dependencies {
                implementation(project(":mockative"))
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}

//afterEvaluate {
//    kotlin.targets["metadata"].compilations.forEach { compilation ->
//        compilation.compileTaskProvider {
//            compilation.compileDependencyFiles = files(
//                compilation.compileDependencyFiles.filterNot { it.absolutePath.endsWith("klib/common/stdlib") }
//            )
//        }
//    }
//}
