package io.mockative

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File

abstract class MockativeProcessRuntimeTask : DefaultTask() {
    private val runtimeSrcDir = File("/Users/nicklas/git/Mockative/mockative/mockative-test/src")

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun run() {
        val mockativeDir = project.mockativeDir

        println("Deleting runtime from '$mockativeDir'")
        mockativeDir.deleteRecursively()

        val testTasks = project.testTasks
        if (testTasks.isNotEmpty()) {
            println("Running test tasks '${testTasks.joinToString { it.name }}' - copying runtime from resources")
            for (sourceSet in project.kotlinExtension.sourceSets) {
                // TODO Move content of `runtimeSrcDir` to `/resources` of Gradle plugin
                val src = File(runtimeSrcDir, sourceSet.name)
                if (src.exists()) {
                    val dst = File(project.mockativeDir, sourceSet.name)
                    println("Copying '$src' to '$dst'")

                    src.copyRecursively(dst, overwrite = true)
                } else {
                    println("Skipping '$src' as it does not exist")
                }

                if (sourceSet.name == "jvmMain" || sourceSet.name == "androidMain") {
                    sourceSet.dependencies {
                        implementation(kotlin("reflect"))
                        implementation("org.objenesis:objenesis:3.3")
                        implementation("org.javassist:javassist:3.29.2-GA")
                    }
                }
            }
        } else {
            println("No test tasks detected - runtime will not be copied.")
        }
    }

    private fun println(message: String) {
//        kotlin.io.println("[MockativeProcessRuntimeTask] $message")
    }
}
