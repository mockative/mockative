package io.mockative

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.AbstractTestTask
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
        val ksp = project.extensions.getByType(KspExtension::class.java)
        ksp.arg("io.mockative:mockative:disabled", "false")
    }
}
