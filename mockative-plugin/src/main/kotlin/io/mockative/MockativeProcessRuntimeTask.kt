package io.mockative

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

abstract class MockativeProcessRuntimeTask : DefaultTask() {
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

            val resources = ResourceManager({}.javaClass)

            val dst = project.mockativeDir.toPath()
            println("Copying resources to '$dst'")
            resources.copyRecursively("/src", dst)

            for (sourceSet in project.kotlinExtension.sourceSets) {
                if (sourceSet.name == "jvmMain" || sourceSet.name == "androidMain") {
                    println("Adding JVM runtime dependencies to source set '${sourceSet.name}'")

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

    private fun println(message: Any) {
        // kotlin.io.println("[MockativeProcessRuntimeTask] $message")
    }
}
