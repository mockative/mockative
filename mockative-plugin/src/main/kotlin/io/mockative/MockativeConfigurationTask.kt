package io.mockative

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

/**
 * Uses JVM properties to configure mock generation using Mockative when a test task is being executed. When no test
 * task is being executed any previously generated mocks will be deleted, which prevents including mocks in production
 * builds.
 */
abstract class MockativeConfigurationTask : DefaultTask() {
    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun run() {
        val testTasks = project.testTasks
        if (testTasks.isNotEmpty()) {
            val testTaskString = testTasks.joinToString(", ") { it.name }
            println("Enabling due to test tasks detected: $testTaskString")

            val ksp = project.extensions.getByType(KspExtension::class.java)
            ksp.arg("io.mockative:tasks", testTasks.joinToString { it.name })
        } else {
            println("Disabling due to no test tasks detected")
        }
    }

    private fun println(message: String) {
//        kotlin.io.println("[MockativeConfigurationTask] $message")
    }
}
