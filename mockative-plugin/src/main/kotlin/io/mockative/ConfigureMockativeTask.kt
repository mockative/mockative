package io.mockative

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Uses JVM properties to configure mock generation using Mockative when a test task is being executed. When no test
 * task is being executed any previously generated mocks will be deleted, which prevents including mocks in production
 * builds.
 */
abstract class ConfigureMockativeTask : DefaultTask() {
    @get:Input
    val generate: Boolean
        get() = project.testTasks.isNotEmpty()

    @get:OutputFile
    val conf: File = project.mockativeConfigurationFile

    private val confContent: String
        get() = "io.mockative.enabled=$generate"

    @TaskAction
    fun run() {
        val testTasks = project.testTasks
        if (testTasks.isNotEmpty()) {
            val testTaskString = testTasks.joinToString(", ") { it.name }
            println("[Mockative] Enabling due to test tasks detected: $testTaskString")

            val ksp = project.extensions.getByType(KspExtension::class.java)
            ksp.arg("io.mockative:tasks", testTasks.joinToString { it.name })
        } else {
            println("[Mockative] Disabling due to no test tasks detected")
        }

        conf.writeText(confContent)
    }
}
