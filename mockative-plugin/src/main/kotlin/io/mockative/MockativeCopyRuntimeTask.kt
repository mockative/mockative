package io.mockative

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.sources.android.findKotlinSourceSet
import java.io.File

abstract class MockativeCopyRuntimeTask : DefaultTask() {
    private fun println(message: String) {
        kotlin.io.println("[MockativeCopyRuntimeTask] $message")
    }

    private val generate: Boolean
        get() = project.testTasks.isNotEmpty()

    private val runtimeSrcDir = File("/Users/nicklas/git/Mockative/mockative/mockative-test/src")

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun run() {
        project.mockativeDir.deleteRecursively()

        copyRuntime(project.kotlinExtension.sourceSets.map { it.name }.toSet())
    }

    private fun deleteRuntime() {
        println("Deleting runtime...")

        val sourceSets = configureSrcDirs(include = false)
        deleteRuntime(sourceSets)
    }

    private fun deleteRuntime(includedSrcDirs: Set<String>) {
        println("  Deleting runtime...")
        for (sourceSet in includedSrcDirs) {
            val src = File(project.mockativeDir, sourceSet)
            if (src.exists()) {
                if (src.deleteRecursively()) {
                    println("    Deleted '$src'")
                } else {
                    println("    Failed to delete '$src'")
                }
            } else {
                println("    Skipping '$src' as it does not exist")
            }
        }
    }

    private fun copyRuntime() {
        println("Copying runtime...")

        val sourceSets = configureSrcDirs(include = false)
        copyRuntime(sourceSets)
    }

    private fun copyRuntime(includedSrcDirs: Set<String>) {
        println("  Copying runtime...")

        for (sourceSet in includedSrcDirs) {
            val src = File(runtimeSrcDir, sourceSet)
            if (src.exists()) {
                val dst = File(project.mockativeDir, sourceSet)
                println("    Copying '$src' to '$dst'")

                src.copyRecursively(dst, overwrite = true)
            } else {
                println("    Skipping '$src' as it does not exist")
            }
        }
    }

    private fun configureSrcDirs(include: Boolean = true): Set<String> {
        println("  Configuring srcDirs...")
        val kmp = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        println("    Targets: ${kmp.targets.size}")

        return kmp.targets
            .mapNotNull { target -> target.compilations.findByName("main") }
            .flatMap { compilation -> compilation.allKotlinSourceSets }
            .map { sourceSet -> sourceSet.name }
            .toSet()

//        val includedSourceSets = mutableSetOf<String>()
//
//        kmp.targets.forEach { target ->
//            val mainCompilation = target.compilations.findByName("main")
//            if (mainCompilation == null) {
//                println("    No main compilation found for target '${target.name}'. Compilations: ${target.compilations.joinToString { it.name }}")
//                return@forEach
//            }
//
//            println("      Target: '${target.name}'")
//
//            val allKotlinSourceSets = mainCompilation.allKotlinSourceSets
//            println("        allKotlinSourceSets: ${allKotlinSourceSets.joinToString { it.name }}")
//
//            allKotlinSourceSets.forEach { kotlinSourceSet ->
//                includedSourceSets.add(kotlinSourceSet.name)
//
//                val mockativeMainSrcDir = File(project.mockativeDir, "${kotlinSourceSet.name}/kotlin")
//                if (include) {
//                    println("          ${kotlinSourceSet.name} += '$mockativeMainSrcDir'")
//                } else {
//                    println("          ${kotlinSourceSet.name}: '$mockativeMainSrcDir'")
//                }
//            }
//        }
//
//        return includedSourceSets
    }
}
