package io.mockative

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class MockativeProcessRuntimeTask : DefaultTask() {
    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun run() {
        project.info("io.mockative.enabled=${project.findProperty("io.mockative.enabled")}")

        project.info("gradle.startParameter.taskNames=${project.gradle.startParameter.taskNames.joinToString()}")
        project.info("gradle.taskGraph.allTasks=${project.gradle.taskGraph.allTasks.toDescription()}")

        project.info("mockativeDir: ${project.mockativeDir}")

        project.info("verificationTasks: ${project.verificationTasks.toDescription()}")
        project.info("testTasks: ${project.testTasks.toDescription()}")
        project.info("deviceTestTasks: ${project.deviceTestTasks.toDescription()}")

        project.info("isMockativeEnabled: ${project.isMockativeEnabled}")
        project.info("isMockativeDisabled: ${project.isMockativeDisabled}")

        project.info("isRunningTestPrefix: ${project.isRunningTestPrefix}")
        project.info("isRunningTestSuffix: ${project.isRunningTestSuffix}")
        project.info("isRunningTestsSuffix: ${project.isRunningTestsSuffix}")
        project.info("isRunningCompilingLinter: ${project.isRunningCompilingLinter}")

        val mockativeDir = project.mockativeDir

        project.debug("Deleting runtime from '$mockativeDir'")
        mockativeDir.deleteRecursively()

        project.runMockative {
            project.debug("Copying runtime from resources")

            val resources = ResourceManager(project, {}.javaClass)

            val dst = mockativeDir.toPath()
            project.debug("Copying resources to '$dst'")
            resources.copyRecursively("/src", dst)

            // This check enables linters that perform Kotlin compilation like Detekt, by replacing with Android
            // implementation of `mock` with a stub, since the Android Gradle Plugin prohibits modifying Android
            // dependencies during a task action.
            if (project.isRunningCompilingLinter) {
                project.info("Replacing android implementation with stub because a linter is detected")
                resources.copyRecursively("/src/androidStubMain", dst.resolve("androidMain"))
            }
        }
    }
}
