plugins {
    id("convention.multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

//afterEvaluate {
//    kotlin.targets["metadata"].compilations.forEach { compilation ->
//        compilation.compileTaskProvider {
//            compilation.compileDependencyFiles = files(
//                compilation.compileDependencyFiles.filterNot { it.absolutePath.endsWith("klib/common/stdlib") }
//            )
//        }
//    }
//}
